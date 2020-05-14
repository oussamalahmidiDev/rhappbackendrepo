package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Salarie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/salarie/api/absences")
@CrossOrigin("*")
public class AbsenceAppController {

    Logger log = LoggerFactory.getLogger(AbsenceAppController.class);


    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }


    @GetMapping()
    public List<Absence> getAbsences () {

        return getProfile().getAbsences();
    }
}
