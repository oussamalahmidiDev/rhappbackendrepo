package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.Direction;
import com.gi.rhapp.models.Service;
import com.gi.rhapp.repositories.DirectionRepository;
import com.gi.rhapp.repositories.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rh/api")
@CrossOrigin("*")
public class RhServicesDirectionsController {
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private DirectionRepository directionRepository;


    @GetMapping("/services")
    public List<Service> getServices() {
        return serviceRepository.findAll();
    }


    @GetMapping("/directions")
    public List<Direction> getDirections() {
        return directionRepository.findAll();
    }


}
