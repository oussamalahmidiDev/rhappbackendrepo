package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.controllers.salarie.SalarieAppController;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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



//    *********************************************** API get all Salaries *********************************************************************

    @GetMapping(value = "/salaries") //works
    public List<Salarie>  getSalaries(){

        return salarieRepository.findAll();
    }

//    **************************************************************************************************************************************************
    //    *********************************************** API get Salarie by id ******************************************************************

    @GetMapping(value = "/salaries/{id}") //wroks
    public Salarie getOneSalarie(@PathVariable(name = "id")Long id){
//        mailService.sendVerificationMail(salarie); just for test
            return salarieRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie avec id = " + id + " est introuvable."));

    }
//    *******************************************************************************************************************************************************
    //    *********************************************** API get Salaries by theirs infos ******************************************************************

    @GetMapping(value = "/salaries/search") //works
    public List<Salarie> searchSalaries(@RequestBody(required = false) Salarie salarie){
            return salarieRepository.findAll(Example.of(salarie));
    }


    //    **************************************************************************************************************************************************
    //    *********************************************** API get all absences ******************************************************************

    @GetMapping(value = "/absences") //works
    public List<Absence> getAbsences(){
            return absenceRepository.findAll();
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "Absences" of salarie by id ******************************************************************


    @GetMapping(value = "/salaries/{id}/absences") // works
    public List<Absence> getAbsences(@PathVariable(value = "id") Long id) {
            return getOneSalarie(id).getAbsences();

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "avantagesNat" of salarie by id ******************************************************************

    @GetMapping(value = "/salaries/{id}/avantages") //works
    public Collection<AvantageNat> getAvantages(@PathVariable(value = "id") Long id){
            return getOneSalarie(id).getAvantages();
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get all "retraites" ******************************************************************

    @GetMapping(value = "/retraites") //works
    public List<Retraite> getRetraites(){
            return  retraiteRepository.findAll();

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "retraite"  by id  ******************************************************************

    @GetMapping(value = "/retraites/{id}") // works
    public Retraite getOneRetraite(@PathVariable(value = "id")Long id){
        try{
            return retraiteRepository.findById(id).get();

        }catch (NoSuchElementException e){
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND, "Le retraite avec id = " + id + " est introuvable.");

        }
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API add "retraite"  ******************************************************************

    @PostMapping(value = "/retraites/ajouter") //works
    public Retraite addRetraite(@RequestBody Retraite retraite){

       return retraiteRepository.save(retraite);

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API modify "retraite"   ******************************************************************

    @PutMapping(value = "/retraites/{id}/modifier" ) //not yet
    @Transactional
    public ResponseEntity<?> addRetraite(@PathVariable(value = "id")Long id , @RequestBody Retraite retraite){
        try{
            Retraite retraiteTomodify = retraiteRepository.findById(id).get();
            retraite.setId(id);
//            retraite.setDateModification(new Date());
//            if(retraite.getEtat() !=null) newRetraite.setEtat(retraite.getEtat());
//            if(retraite.getDateRetraite()!=null) newRetraite.setDateRetraite(retraite.getDateRetraite());
//            if(retraite.getDateCreation()!=null) newRetraite.setDateCreation(retraite.getDateCreation());
//            if(retraite.getDateValidation()!=null) newRetraite.setDateValidation(retraite.getDateValidation());
//            if(retraite.getType()!=null) newRetraite.setType(retraite.getType());
//            if(retraite.getRemarques()!=null) newRetraite.setRemarques(retraite.getRemarques());
//            copyNonNullProperties(retraite,oldRetraite);
            BeanUtils.copyProperties(retraite,retraiteTomodify);

            return ResponseEntity.ok("");

        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le retraite avec id = " + id + " est introuvable.");

        }
    }
}

