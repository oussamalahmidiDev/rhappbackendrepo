package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
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



//    *********************************************** API get all Salaries *********************************************************************

    @GetMapping() //works
    public List<Salarie>  getSalaries(){
        return salarieRepository.findAll();
    }

//    **************************************************************************************************************************************************
    //    *********************************************** API get Salarie by id ******************************************************************

    @GetMapping(value = "/{id}") //wroks
    public Salarie getOneSalarie(@PathVariable(name = "id")Long id){
//        mailService.sendVerificationMail(salarie); just for test
            return salarieRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));
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
            .email(request.getEmail())
            .nom(request.getNom())
            .prenom(request.getPrenom())
            .build();

        user = userRepository.save(user);
        mailService.sendVerificationMail(user);

        Salarie newSalarie = Salarie.builder()
            .numSomme(request.getNumSomme())
            .direction(request.getDirection())
            .service(request.getService())
            .solde(request.getSolde())
            .user(user)
            .build();

        return salarieRepository.save(newSalarie);

    }

}

