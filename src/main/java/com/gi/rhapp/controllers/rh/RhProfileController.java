package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.controllers.salarie.SalarieAppController;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.aspectj.weaver.loadtime.Agent;
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
@RequestMapping("/rh/api/profile")
@CrossOrigin("*")
public class RhProfileController {

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



//    *********************************************** API get rh profile *********************************************************************

    @GetMapping(value = "/{id}") //works
    public User  getProfile(@PathVariable(value = "id")Long id){

        return userRepository.findByIdAndRole(id,Role.RH);
    }


}

