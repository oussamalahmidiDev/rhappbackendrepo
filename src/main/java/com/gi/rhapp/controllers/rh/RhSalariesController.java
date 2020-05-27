package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.EtatRetraite;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.Download;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Path;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rh/api/salaries")
@CrossOrigin("*")
public class RhSalariesController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvantageNatRepository avantageNatRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private TypeRetraiteRepository typeRetraiteRepository;

    @Autowired
    private TypeAvantageRepository typeAvantageRepository;

    @Autowired
    private Download downloadService;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AuthService authService;

    private String service = "Gestion des RH - Gestion des salariés";




//    *********************************************** API get all Salaries *********************************************************************

    @GetMapping() //works
    public List<Salarie>  getSalaries(){
        return salarieRepository.findAllByOrderByDateCreationDesc();
    }

//    **************************************************************************************************************************************************
    //    *********************************************** API get Salarie by id ******************************************************************

    @GetMapping(value = "/{id}") //wroks
    public Salarie getOneSalarie(@PathVariable(name = "id")Long id){
//        mailService.sendVerificationMail(salarie); just for test
            return salarieRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));
    }

    @GetMapping("{id}/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(HttpServletRequest request, @PathVariable("id") Long id, @PathVariable("filename") String filename) {
        User user = getOneSalarie(id).getUser();

        if (user.getPhoto() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");

        if (!user.getPhoto().equals(filename))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Resource resource = downloadService.downloadImage(user.getPhoto());

        // setting content-type header
        String contentType = null;
        try {
            // setting content-type header according to file type
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Type indéfini.");
        }
        // setting content-type header to generic octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }
//    *******************************************************************************************************************************************************
    //    *********************************************** API get Salaries by theirs infos ******************************************************************

    @GetMapping(value = "/{id}/conges")
    public List<Conge> getSalarieConges(@PathVariable(name = "id") Long id) {
        return this.getOneSalarie(id).getConges();
    }

    @GetMapping("/{id}/absences")
    public List<Absence> getSalarieAbsences(@PathVariable(name = "id") Long id) {
        return this.getOneSalarie(id).getAbsences();
    }

    @GetMapping(value = "/{id}/avantages") //works
    public Collection<AvantageNat> getAvantages(@PathVariable(value = "id") Long id){
        return getOneSalarie(id).getAvantages();
    }

    @PostMapping(value = "/{id}/retraite/create") //works
    public Retraite createRetraite (@PathVariable(value = "id") Long id, @RequestBody Retraite retraite){
        Salarie salarie = getOneSalarie(id);
        retraite.setSalarie(salarie);
        TypeRetraite type = retraite.getType();
        if (type.getId() == null)
            retraite.setType(typeRetraiteRepository.save(type));

        retraite.setSalarie(salarie);
        return retraiteRepository.save(retraite);
    }

    @PostMapping("/{id}/avantages/create")
    public AvantageNat createAvantage(@PathVariable(value = "id") Long id, @RequestBody AvantageNat avantageNat) {
        Salarie salarie = getOneSalarie(id);
        TypeAvantage type = avantageNat.getType();
        if (type.getId() == null)
            avantageNat.setType(typeAvantageRepository.save(type));

        avantageNat.setSalarie(salarie);
        return avantageNatRepository.save(avantageNat);
    }

    @PostMapping("/{id}/avantages/retirer")
    public Salarie retirerAvantages(@PathVariable(value = "id") Long id, @RequestBody List<AvantageNat> avantages) {
        Salarie salarie = getOneSalarie(id);
        Retraite retraite = salarie.getRetraite();
        if (retraite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);


        for (AvantageNat element: avantages) {
            element = avantageNatRepository.findById(element.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
            );
            System.out.println("AVANTAGE : " + element.getSpecification());
            if (element.getSalarie() == salarie) {
                element.setRetire(true);
                avantageNatRepository.save(element);
            }
        }

        retraite.setEtat(EtatRetraite.PENDING_VALID);
        retraiteRepository.save(retraite);

        activityRepository.save(
            Activity.builder()
                .evenement("Retraite des avantages en nature de : " + retraite.getSalarie().getUser().getFullname())
                .service(this.service + " : Retraite des avantages")
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        return salarie;
    }

    @PostMapping(value = "/{id}/retraite/valider") //works
    public Retraite createRetraite (@PathVariable(value = "id") Long id){
        Retraite retraite = getOneSalarie(id).getRetraite();
        if (retraite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        retraite.setEtat(EtatRetraite.VALID);
        retraite.setDateValidation(new Date());

        retraite = retraiteRepository.save(retraite);

        activityRepository.save(
            Activity.builder()
                .evenement("Validation de la retraite de : " + retraite.getSalarie().getUser().getFullname())
                .service(this.service + " : Retraites")
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        return retraite;
    }


    @GetMapping(value = "/search") //works
    public List<Salarie> searchSalaries(@RequestParam String query){
//            return salarieRepository.findAll(Example.of(salarie));

//        Alternative method :
        return salarieRepository.findAllByUserNomContainingIgnoreCaseOrUserPrenomContainingIgnoreCaseOrUserEmailContainingIgnoreCaseOrNumSommeContainingIgnoreCaseOrServiceNomContainingIgnoreCaseOrDirectionNomContainingIgnoreCase(
            query,
            query,
            query,
            query,
            query,
            query
        );
    }

    @PostMapping(value = "/create") //works
    public Salarie createSalarie(@RequestBody SalarieRequest request) {
        if (userRepository.findByEmail(request.getEmail()) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà");

        User user = User.builder()
            .email(request.getEmail().trim().toLowerCase())
            .nom(request.getNom())
            .prenom(request.getPrenom())
            .role(Role.SALARIE)
            .build();

        user = userRepository.save(user);

        Salarie newSalarie = Salarie.builder()
            .numSomme(request.getNumSomme())
            .direction(request.getDirection())
            .service(request.getService())
            .solde(request.getSolde())
            .user(user)
            .build();

        newSalarie = salarieRepository.save(newSalarie);

        mailService.sendVerificationMail(user);

        activityRepository.save(
            Activity.builder()
                .evenement("Enregistrement d'un nouveau salarié : " + newSalarie.getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        return newSalarie;
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteAbsence(@PathVariable Long id) {
        Salarie salarie = getOneSalarie(id);
        userRepository.delete(salarie.getUser());

        activityRepository.save(
            Activity.builder()
                .evenement("Suppression du salarié : " + salarie.getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok("");
    }

}

