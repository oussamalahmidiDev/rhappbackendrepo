package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.Direction;
import com.gi.rhapp.models.Poste;
import com.gi.rhapp.models.Service;
import com.gi.rhapp.repositories.DirectionRepository;
import com.gi.rhapp.repositories.PosteRepository;
import com.gi.rhapp.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
