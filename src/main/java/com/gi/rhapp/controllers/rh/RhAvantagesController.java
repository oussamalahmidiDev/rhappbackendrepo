package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.models.*;
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
@RequestMapping("/rh/api/avantages")
@CrossOrigin("*")
public class RhAvantagesController {

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
    private TypeAvantageRepository typeAvantageRepository;



//    *********************************************** API get all Avantages *********************************************************************

    @GetMapping() //works
    public List<AvantageNat>  getAvantages(){
        return avantageNatRepository.findAll();
    }

    @GetMapping("/types")
    public List<TypeAvantage> getAvantageTypes () {
        return typeAvantageRepository.findAll();
    }

}

