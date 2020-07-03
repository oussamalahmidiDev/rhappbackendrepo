package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.EtatRetraite;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.*;
import com.sipios.springsearch.anotation.SearchSpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.Path;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private ServiceRepository serviceRepository;

    @Autowired
    private DirectionRepository directionRepository;

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
    private NotificationService notificationService;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private AuthService authService;

    private String service = "Gestion des RH - Gestion des salariés";


//    *********************************************** API get all Salaries *********************************************************************

    @GetMapping() //works
    public List<Salarie> getSalaries() {
        return salarieRepository.findAllByOrderByDateCreationDesc();
    }

//    **************************************************************************************************************************************************
    //    *********************************************** API get Salarie by id ******************************************************************

    @GetMapping(value = "/{id}") //wroks
    public Salarie getOneSalarie(@PathVariable(name = "id") Long id) {
//        mailService.sendVerificationMail(salarie); just for test
        return salarieRepository.findAllByOrderByDateCreationDesc().stream()
            .filter(salarie -> salarie.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));
//        return salarieRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));
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

    @GetMapping("{id}/cv/{filename}")
    public ResponseEntity<Resource> donwloadCV(HttpServletRequest request, @PathVariable("id") Long id, @PathVariable("filename") String filename) {
        Salarie salarie = getOneSalarie(id);

        if (salarie.getCv() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Pas de CV disponible.");

        if (!salarie.getCv().equals(filename))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Resource resource = downloadService.downloadCV(salarie.getCv());

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

    @GetMapping("/diplomes/{id}/{filename}")
    public ResponseEntity<Resource> donwloadDiplome(HttpServletRequest request, @PathVariable("id") Long id, @PathVariable("filename") String filename) {
        Diplome diplome = diplomeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diplome introuvable")
        );
//
//        if (salarie.getCv() == null)
//            throw new ResponseStatusException(HttpStatus.OK, "Pas de CV disponible.");

        if (!diplome.getPath().equals(filename))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        Resource resource = downloadService.downloadDiplome(diplome.getPath());

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
    public Collection<AvantageNat> getAvantages(@PathVariable(value = "id") Long id) {
        return getOneSalarie(id).getAvantages();
    }

    @PostMapping(value = "/{id}/retraite/create") //works
    public Retraite createRetraite(@PathVariable(value = "id") Long id, @RequestBody Retraite retraite) {
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

        for (AvantageNat element : avantages) {
            element = avantageNatRepository.findById(element.getId()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST)
            );
            System.out.println("AVANTAGE : " + element.getSpecification());
            if (element.getSalarie() == salarie) {
                element.setRetire(true);
                avantageNatRepository.save(element);

                activitiesService.saveAndSend(
                    Activity.builder()
                        .evenement("Retraite de l'avantage en nature " + element.getSpecification() + " de : " + element.getSalarie().getUser().getFullname())
                        .service(this.service + " : Retraite des avantages")
                        .user(authService.getCurrentUser())
                        .scope(Role.ADMIN)
                        .build()
                );
            }
        }
//        if (retraite != null) {
//            retraite.setEtat(EtatRetraite.PENDING_VALID);
//            retraiteRepository.save(retraite);
//        }


        return salarie;
    }

    @PostMapping(value = "/{id}/retraite/valider") //works
    public Retraite validerRetraite(@PathVariable(value = "id") Long id, @RequestBody Retraite request) {
        Retraite retraite = getOneSalarie(id).getRetraite();
        if (retraite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        retraite.setEtat(EtatRetraite.VALID);
        retraite.setDateValidation(LocalDate.now());
        retraite.setRemarques(request.getRemarques());

        retraiteRepository.save(retraite);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Validation de la retraite de : " + retraite.getSalarie().getUser().getFullname())
                .service(this.service + " : Retraites")
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        return retraite;
    }

    @PutMapping(value = "/{id}/retraite/modifier") //works
    public Retraite modifierRetraite(@PathVariable(value = "id") Long id, @RequestBody Retraite request) {
        Salarie salarie = getOneSalarie(id);
        Retraite retraite = salarie.getRetraite();

        if (retraite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        retraite.setType(request.getType());
        retraite.setReference(request.getReference());
        retraite.setDateRetraite(request.getDateRetraite());

        salarie.setRetraite(retraite);
        retraiteRepository.save(retraite);

        salarieRepository.save(salarie);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Modification des informations de la retraite de : " + salarie.getUser().getFullname())
                .service(this.service + " : Retraites")
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        return retraite;
    }

    @DeleteMapping("/{id}/retraite/supprimer")
    @ResponseStatus(HttpStatus.OK)
    public void supprimerRetraite(@PathVariable Long id) {
        Salarie salarie = getOneSalarie(id);
        Retraite retraite = salarie.getRetraite();
        if (retraite == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        retraiteRepository.delete(retraite);

        salarie.setRetraite(null);
        salarieRepository.save(salarie);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Suppression de la retraite de : " + retraite.getSalarie().getUser().getFullname())
                .service(this.service + " : Retraites")
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
    }


    @GetMapping(value = "/search") //works
    public List<Salarie> searchSalaries(@SearchSpec Specification<Salarie> specifications) {
        return salarieRepository.findAll(Specification.where(specifications));
    }

    @PostMapping(value = "/create") //works
    public Salarie createSalarie(@Valid @RequestBody SalarieRequest request) {

        if (userRepository.findByEmail(request.getEmail()) != null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Cet email existe déjà");

        if (request.getDateRecrutement().isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La date de recrutement doit être une date antérieure");

        if (request.getDateNaissance().after(new Date()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La date de naissance doit être une date antérieure");



        User user = User.builder()
            .email(request.getEmail().trim().toLowerCase())
            .nom(request.getNom())
            .prenom(request.getPrenom())
            .role(Role.SALARIE)
            .build();

        userRepository.save(user);

        Service service = request.getService();
        if (service.getId() == null) {
            serviceRepository.save(service);
            activitiesService.saveAndSend(
                Activity.builder()
                    .evenement("Création d'un nouveau service : " + service.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        Direction direction = request.getDirection();
        if (direction.getId() == null) {
            directionRepository.save(direction);
            activitiesService.saveAndSend(
                Activity.builder()
                    .evenement("Création d'une nouvelle direction : " + direction.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        Salarie newSalarie = Salarie.builder()
            .numSomme(request.getNumSomme())
            .direction(direction)
            .service(service)
            .dateRecrutement(request.getDateRecrutement())
            .dateNaissance(request.getDateNaissance())
//            .solde(request.getSolde())
            .user(user)
            .build();

        salarieRepository.save(newSalarie);

        mailService.sendVerificationMail(user);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Enregistrement d'un nouveau salarié : " + newSalarie.getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        List<User> receivers = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE).stream()
            .filter(agent -> !agent.equals(authService.getCurrentUser()))
            .collect(Collectors.toList());


        Notification notification = Notification.builder()
            .content(String.format("%s a enregistré un nouveau salarié %s", authService.getCurrentUser().getFullname(), newSalarie.getUser().getFullname()))
            .build();

        notificationService.send(notification, receivers.toArray(new User[receivers.size()]));

        Notification notificationToSalarie = Notification.builder()
            .content(String.format("Bienvenue sur Rehapp !"))
            .build();

        notificationService.send(notificationToSalarie, newSalarie.getUser());

        return newSalarie;
    }

    @PutMapping("/{id}/modifier")
    public Salarie modifierSalarie(@PathVariable Long id, @Valid @RequestBody SalarieRequest request) {

        if (request.getDateRecrutement().isAfter(LocalDate.now()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La date de recrutement doit être une date antérieure");

        if (request.getDateNaissance().after(new Date()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La date de naissance doit être une date antérieure");

        Salarie salarie = getOneSalarie(id);
        salarie.getUser().setNom(request.getNom());
        salarie.getUser().setPrenom(request.getPrenom());
        salarie.getUser().setEmail(request.getEmail());

        Service service = request.getService();
        if (service.getId() == null) {
            serviceRepository.save(service);
            activitiesService.saveAndSend(
                Activity.builder()
                    .evenement("Création d'un nouveau service : " + service.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        Direction direction = request.getDirection();
        if (direction.getId() == null) {
            directionRepository.save(direction);
            activitiesService.saveAndSend(
                Activity.builder()
                    .evenement("Création d'une nouvelle direction : " + direction.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        salarie.setDirection(direction);
        salarie.setService(service);
//        salarie.setSolde(request.getSolde());
        salarie.setDateNaissance(request.getDateNaissance());
        salarie.setDateRecrutement(request.getDateRecrutement());
        return salarieRepository.save(salarie);
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteSalarie(@PathVariable Long id, @RequestBody Salarie request) {
        Salarie salarie = getOneSalarie(id);
        salarie.setRaisonSuppression(request.getRaisonSuppression());

        salarie.setDeleted(true);
        userRepository.save(salarie.getUser());
//        userRepository.delete(salarie.getUser());

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Suppression du salarié : " + salarie.getUser().getFullname() + " pour la raison : " + request.getRaisonSuppression())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok(salarie);
    }

    @DeleteMapping("/{id}/restore")
    public ResponseEntity<?> cancelDeleteSalarie(@PathVariable Long id) {
        Salarie salarie = getOneSalarie(id);
        salarie.setRaisonSuppression(null);

        salarie.setDeleted(false);
        userRepository.save(salarie.getUser());

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Annulation de la suppression du salarié : " + salarie.getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok(salarie);
    }


}

