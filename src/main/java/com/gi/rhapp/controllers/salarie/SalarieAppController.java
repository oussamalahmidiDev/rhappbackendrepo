package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Conge;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.AbsenceRepository;
import com.gi.rhapp.repositories.CongeRepository;
import com.gi.rhapp.repositories.SalarieRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.MailService;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/salarie/api")
public class SalarieAppController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private MailService mailService;

    Logger log = LoggerFactory.getLogger(SalarieAppController.class);

    @GetMapping("/profil")
    public Salarie getProfile () {
        return salarieRepository.findById(1L).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id invalide")
        );
    }

    @GetMapping("/absences")
    public List<Absence> getAbsences () {
        return getProfile().getAbsences();
    }

    @PostMapping("/absences/create")
    public Absence createAbsence(@RequestBody Absence absence) {
        absence.setSalarie(getProfile());
        return absenceRepository.save(absence);
    }

    @PostMapping("/profil/modifier")
    public Salarie modifierProfil(@RequestBody Salarie profil) {
        return salarieRepository.save(profil);
    }

    @GetMapping("/conges")
    public List<Conge> getConges () {
        return getProfile().getConges();
    }

    @PostMapping("/conges/create")
    public Conge createConge (@RequestBody Conge conge) {
        if (getProfile().getSolde() < conge.getDuree())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Vous ne pouvez pas dépasser %d jours du congé.", getProfile().getSolde()));

        conge.setSalarie(getProfile());
        return congeRepository.save(conge);
    }

//   to test email service
    @GetMapping("/confirm")
    public ResponseEntity<String> testConfirmation() {
        mailService.sendVerificationMail(getProfile().getUser());
        return ResponseEntity.ok("");
    }




}
