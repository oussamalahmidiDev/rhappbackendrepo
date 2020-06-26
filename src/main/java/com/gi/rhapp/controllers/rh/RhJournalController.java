package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rh/api/activities")
public class RhJournalController {
    @Autowired
    private ActivityRepository repository;

    @Autowired
    private AuthService authService;

    @GetMapping()
    public Page<Activity> getActivities(@RequestParam("limit") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        if (authService.getCurrentUser().getRole() == Role.ADMIN)
            return repository.findAll(pageable);

        return repository.findAllByScope(Role.RH, pageable);
    }

    @GetMapping("/personnal")
    public Page<Activity> getPersonnalActivities(@RequestParam("limit") int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "timestamp"));
        return repository.findAllByUser(authService.getCurrentUser(), pageable);
    }

}
