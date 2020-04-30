package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.AvantageNat;
import com.gi.rhapp.models.Retraite;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

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

    @GetMapping(value = "/search") //works
    public List<Salarie> searchSalaries(@RequestBody(required = false) Salarie salarie){
            return salarieRepository.findAll(Example.of(salarie));
    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "Absences" of salarie by id ******************************************************************


    @GetMapping(value = "/{id}/absences") // works
    public List<Absence> getAbsences(@PathVariable(value = "id") Long id) {
            return getOneSalarie(id).getAbsences();

    }

    //    **************************************************************************************************************************************************
    //    *********************************************** API get "avantagesNat" of salarie by id ******************************************************************

    @GetMapping(value = "/{id}/avantages") //works
    public Collection<AvantageNat> getAvantages(@PathVariable(value = "id") Long id){
            return getOneSalarie(id).getAvantages();
    }


}

