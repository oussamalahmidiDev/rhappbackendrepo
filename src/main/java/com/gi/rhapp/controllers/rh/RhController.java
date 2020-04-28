package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.controllers.salarie.SalarieAppController;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/rh/api")
public class RhController {

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

    Logger log = LoggerFactory.getLogger(SalarieAppController.class);



//    *********************************************** API get all Salaries *********************************************************************

    @GetMapping(value = "/salaries")
    public List<Salarie>  getSalaries(){

        return salarieRepository.findAll();
    }

//    **************************************************************************************************************************************************
    //    *********************************************** API get Salarie by id ******************************************************************

    @GetMapping(value = "/salaries/{id}")
    public Salarie getOneSalarie(@PathVariable(name = "id")Long id){
//        mailService.sendVerificationMail(salarie); just for test
            return salarieRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));

    }
//    **************************************************************************************************************************************************
    //    *********************************************** API get Salaries by theirs infos ******************************************************************

    @GetMapping(value = "/salaries/search")
    public List<Salarie> searchSalaries(@RequestBody Salarie salarie){
        try{
            return salarieRepository.findAll(Example.of(salarie));

        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie est introuvable.");
        }
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get all absences ******************************************************************

    @GetMapping(value = "/absences")
    public List<Absence> getAbsences(){
            return absenceRepository.findAll();
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "Absences" of salarie by id ******************************************************************


    @GetMapping(value = "/salaries/{id}/absences")
    public List<Absence> getAbsences(@PathVariable(value = "id") Long id) {
        try{
            return getOneSalarie(id).getAbsences();

        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable.");

        }

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "avantagesNat" of salarie by id ******************************************************************

    @GetMapping(value = "/salaries/{id}/avantages")
    public Collection<AvantageNat> getAvantages(@PathVariable(value = "id") Long id){
        try{
            return getOneSalarie(id).getAvantages();

        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable.");

        }
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get all "retraites" ******************************************************************

    @GetMapping(value = "/retraites")
    public List<Retraite> getRetraites(){
            return  retraiteRepository.findAll();

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "retraite"  by id  ******************************************************************

    @GetMapping(value = "/retraites/{id}")
    public Retraite getOneRetraite(@PathVariable(value = "id")Long id){
        try{
            return getOneSalarie(id).getRetraite();

        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable.");

        }

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API add "retraite"  ******************************************************************

    @PostMapping(value = "/retraites/ajouter")
    public Retraite addRetraite(@RequestBody Retraite retraite){

       return retraiteRepository.save(retraite);

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API modify "retraite"   ******************************************************************

    @PutMapping(value = "/retraites/{id}/modifier")
    public Retraite addRetraite(@PathVariable(value = "id")Long id , @RequestBody Retraite retraite){
        try{
            Retraite newRetraite = getOneSalarie(id).getRetraite();
            retraite.setDateModification(new Date());
            if(retraite.getEtat() !=null) newRetraite.setEtat(retraite.getEtat());
            if(retraite.getDateRetraite()!=null) newRetraite.setDateRetraite(retraite.getDateRetraite());
            if(retraite.getDateCreation()!=null) newRetraite.setDateCreation(retraite.getDateCreation());
            if(retraite.getDateValidation()!=null) newRetraite.setDateValidation(retraite.getDateValidation());
            if(retraite.getType()!=null) newRetraite.setType(retraite.getType());
            if(retraite.getRemarques()!=null) newRetraite.setRemarques(retraite.getRemarques());


            retraiteRepository.save(retraite);
            return retraite;

        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le retraite avec id = " + id + " est introuvable.");

        }
    }
}

