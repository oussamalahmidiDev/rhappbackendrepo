package com.gi.rhapp.controllers.salarie;
import com.gi.rhapp.models.*;
import com.gi.rhapp.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/salarie/api")
@CrossOrigin("*")
public class SalarieAppController {


    @Autowired
    private MailService mailService;

    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }

    Logger log = LoggerFactory.getLogger(SalarieAppController.class);

    @GetMapping("/confirm")
    public ResponseEntity<String> testConfirmation() {
        mailService.sendVerificationMail(getProfile().getUser());
        return ResponseEntity.ok("");
    }

}
