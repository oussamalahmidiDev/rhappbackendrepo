package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.DirectionRepository;
import com.gi.rhapp.repositories.PosteRepository;
import com.gi.rhapp.repositories.SalarieRepository;
import com.gi.rhapp.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rh/api/postes")
@CrossOrigin("*")
public class RhPostesController {

    @Autowired
    private PosteRepository posteRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private DirectionRepository directionRepository;

    @GetMapping()
    public List<Poste> getPostes() {
        return posteRepository.findAll();
    }

    @PostMapping("/create")
    public Poste createPoste(@RequestBody Poste poste) {
        Service service = poste.getService();
        if (service.getId() == null)
            service = serviceRepository.save(service);

        Direction direction = poste.getDirection();
        if (direction.getId() == null)
            direction = directionRepository.save(direction);

        return posteRepository.save(poste);
    }

    @PostMapping("/{id}/affecter")
    public Poste affecterSalarie(@PathVariable("id") Long id, @RequestBody AffectationRequest request) {
        Poste poste = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );
        Salarie selectedSalarie = salarieRepository.findById(request.getSalarieId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        // check if salarie is already affected to another poste, if it's the case it will be overwritten
        if (selectedSalarie.getPoste() != null)
            selectedSalarie.getPoste().setSalarie(null);

        selectedSalarie.setFonction(request.getFonctions());
        selectedSalarie.setDateAffectation(new Date());

        poste.setSalarie(selectedSalarie);

        return posteRepository.save(poste);
    }

    @PutMapping("/{id}/salarie/supprimer")
    public Poste deleteSalarie(@PathVariable("id") Long id) {
        Poste poste = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );
        poste.getSalarie().setFonction(null);
        poste.setSalarie(null);
        return posteRepository.save(poste);
    }
}
