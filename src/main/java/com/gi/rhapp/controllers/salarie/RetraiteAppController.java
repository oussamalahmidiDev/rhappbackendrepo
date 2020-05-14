package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Retraite;
import com.gi.rhapp.models.Salarie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salarie/api/retraites")
@CrossOrigin("*")
public class RetraiteAppController {

    Logger log = LoggerFactory.getLogger(RetraiteAppController.class);


    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }

    @GetMapping()
    public Retraite getRetraites(){ return getProfile().getRetraite();
    }
}
