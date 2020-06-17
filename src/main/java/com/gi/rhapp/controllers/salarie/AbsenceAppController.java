package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.AbsenceRepository;
import com.gi.rhapp.repositories.NotificationRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.ActivitiesService;
import com.gi.rhapp.services.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("/salarie/api/absences")
@CrossOrigin("*")
public class AbsenceAppController {

    Logger log = LoggerFactory.getLogger(AbsenceAppController.class);

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private Upload uploadService;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }


    @GetMapping()
    public List<Absence> getAbsences () {

        return getProfile().getAbsences();
    }

    @PostMapping("/{id}/justifier")
    public Absence justifier(@PathVariable Long id, @RequestPart(name = "justificatif", required = true) MultipartFile justificatif) {
        Absence absence = absenceRepository.findByIdAndSalarie(id, getProfile()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        String filename = uploadService.uploadJustificatif(justificatif);
        absence.setJustificatif(filename);
        absenceRepository.save(absence);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Justification de l'absence de la date " + new SimpleDateFormat("dd/MM/yyyy").format(absence.getDateDebut()))
                .service("Gestion des RH - Gestion des absences")
                .user(getProfile().getUser())
                .scope(Role.SALARIE)
                .build()
        );

        Notification notification = Notification.builder()
            .content(String.format("Le salarié \"%s\" a justifié son absence de la date %s", new SimpleDateFormat("dd/MM/yyyy").format(absence.getDateDebut())))
            .to(userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE))
            .build();

        notificationRepository.save(notification);

        return absence;
    }
}
