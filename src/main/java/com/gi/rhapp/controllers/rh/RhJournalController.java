package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public List<Activity> getActivities() {
        if (authService.getCurrentUser().getRole() == Role.ADMIN)
            return repository.findAllByOrderByTimestampDesc();

        return repository.findAllByScopeOrderByTimestampDesc(Role.RH);
    }

}
