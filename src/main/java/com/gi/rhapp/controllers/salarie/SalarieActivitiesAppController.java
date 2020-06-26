package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salarie/api/activities")
@CrossOrigin("*")
public class SalarieActivitiesAppController {

    @Autowired
    private ProfileAppController profileAppController;

    @Autowired
    private ActivityRepository activityRepository;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }

    @PostMapping("")
    public Page<Activity> getAllActivities(@RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "10") int size){

        return  activityRepository.findAllByUser(getProfile().getUser() , PageRequest.of(page, size, Sort.by("timestamp").descending()));
    }


}
