package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AuthService authService;

    private String service = "Gestion des RH - Gestion des demandes de congés";

    @GetMapping()
    public List<Conge> getConges(){
        return congeRepository.findAllByOrderByDateCreationDesc();
    }

    @PostMapping("/{id}/repondre")
    public Conge repondreConge(@PathVariable("id") Long id, @RequestBody CongeReponseRequest request) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!request.getEtat().equals("ACCEPTED") && !request.getEtat().equals("REJECTED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        conge.setEtat(EtatConge.valueOf(request.getEtat()));
        conge.setReponse(request.getReponse());

        return congeRepository.save(conge);
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
    @PutMapping("/{id}/modifier")
    public Conge modifierConge(@PathVariable Long id, @RequestBody  CongeMaladieRequest request) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!conge.getType().getTypeConge().equals("MALADIE"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        conge.setDateDebut(request.getDateDebut());
        conge.setDateFin(request.getDateFin());
        conge.setMotif(request.getMotif());

        return congeRepository.save(conge);
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteConge(@PathVariable Long id) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!conge.getEtat().equals(EtatConge.ARCHIVED) || !conge.getType().getTypeConge().equals("MALADIE")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de supprimer cette demande de congé à l'instant");
        }
        congeRepository.deleteById(id);

        activityRepository.save(
            Activity.builder()
                .evenement("Suppression de la demande de congé de : " + conge.getSalarie().getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok("");
    }
}
