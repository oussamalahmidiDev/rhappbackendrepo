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
@RequestMapping("/rh/api/absences")
@CrossOrigin("*")
public class RhAbsencesController {

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




    //    **************************************************************************************************************************************************
    //    *********************************************** API get all absences ******************************************************************

    @GetMapping() //works
    public List<Absence> getAbsences(){
            return absenceRepository.findAll();
    }


}

