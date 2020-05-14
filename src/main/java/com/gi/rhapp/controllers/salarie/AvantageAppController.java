package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.AvantageNat;
import com.gi.rhapp.models.Salarie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/salarie/api/avantages")
@CrossOrigin("*")
public class AvantageAppController {
    Logger log = LoggerFactory.getLogger(AvantageAppController.class);


    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }

    @GetMapping()
    public Collection<AvantageNat> getAvantages(){
        return getProfile().getAvantages();
    }
}
