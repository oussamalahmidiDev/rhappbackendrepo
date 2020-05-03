package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.Conge;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rh/api/conges")
@CrossOrigin("*")
public class RhCongesController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private CongeRepository congeRepository;

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

    @GetMapping()
    public List<Conge> getConges(){
        return congeRepository.findAll();
    }
}
