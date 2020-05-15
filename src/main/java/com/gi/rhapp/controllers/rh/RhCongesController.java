package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.models.Conge;
import com.gi.rhapp.models.CongeMaladieRequest;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.TypeConge;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    @GetMapping()
    public List<Conge> getConges(){
        return congeRepository.findAll();
    }

    @PostMapping("create_maladie")
    public Conge createCongeMaladie (@RequestBody CongeMaladieRequest request) {
        Salarie salarie = salarieRepository.findById(request.getSalarieId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        TypeConge maladie = typeCongeRepository.findFirstByTypeConge("MALADIE");
        if (maladie == null)
            maladie = typeCongeRepository.save(TypeConge.builder().typeConge("MALADIE").build());



        Conge conge = Conge.builder()
            .dateDebut(request.getDateDebut())
            .dateFin(request.getDateFin())
            .etat(EtatConge.ACCEPTED)
            .motif(request.getMotif())
            .salarie(salarie)
            .type(maladie)
            .build();

        return congeRepository.save(conge);
    }
}
