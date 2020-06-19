package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rh/api/absences")
@CrossOrigin("*")
@Log4j2
public class RhAbsencesController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvantageNatRepository avantageNatRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private Upload uploadService;

    @Autowired
    private Download downloadService;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    private String service = "Gestion des RH - Gestion des absences";





    //    **************************************************************************************************************************************************
    //    *********************************************** API get all absences ******************************************************************

    @GetMapping() //works
    public List<Absence> getAbsences(){
            return absenceRepository.findAllByOrderByDateCreationDesc();
    }

    @GetMapping("/{id}/justificatif/{filename}")
    public ResponseEntity<Resource> getJustificatif(HttpServletRequest request,@PathVariable("id") Long id, @PathVariable("filename") String filename) {

        Absence absence = absenceRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource introuvable")
        );

        if (absence.getJustificatif() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Cet absence ne possède pas d'un justificatifif.");
        else if (!filename.equals(absence.getJustificatif()))
            throw new ResponseStatusException(HttpStatus.OK, "Fichier introuvable.");


        Resource resource = downloadService.downloadJustificatif(absence.getJustificatif());
        // setting content-type header
        String contentType = null;
        try {
            // setting content-type header according to file type
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Type indéfini.");
        }
        // setting content-type header to generic octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }



        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @PostMapping("/create")
    public Absence createAbsence(
        @RequestParam("salarie_id") Long salarieId,
        @RequestParam("type") String type,
        @RequestParam("dateDebut") Date dateDebut,
        @RequestParam("dateFin") Date dateFin,

        // i used @RequestPart here instead of @RequestParm to mark this param as optional
        @RequestPart(name = "justificatif", required = false) MultipartFile justificatif
    ) {
        Salarie salarie = salarieRepository.findById(salarieId).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        Date dernierAbsence = absenceRepository.getMaxDate(salarieId);

        if (dateDebut.before(salarie.getDateRecrutement()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date de recrutement (" +
                new SimpleDateFormat("dd-MM-yyyy").format(salarie.getDateRecrutement()) + ")");

        salarie.getAbsences().forEach(absence -> {
            if (dateDebut.before(absence.getDateFin()) && dateDebut.after(absence.getDateDebut()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence durant cette période par ce qu'il y a déjà une absence enregsitrée durant cette période " +
                    "(de " + new SimpleDateFormat("dd-MM-yyyy").format(absence.getDateDebut()) + " jusqu'à " + new SimpleDateFormat("dd-MM-yyyy").format(absence.getDateFin()) + ")");
        });

        salarie.getConges().forEach(conge -> {
            if (!conge.getEtat().equals(EtatConge.REJECTED) && !conge.getEtat().equals(EtatConge.PENDING_RESPONSE) && dateDebut.before(conge.getDateFin()) && dateDebut.after(conge.getDateDebut()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence durant cette période par ce que le salarié était en congé durant cette période " +
                    "(de " + new SimpleDateFormat("dd-MM-yyyy").format(conge.getDateDebut()) + " jusqu'à " + new SimpleDateFormat("dd-MM-yyyy").format(conge.getDateFin()) + ")");
        });


//        if (dernierAbsence != null)
//            if (dateDebut.before(dernierAbsence))
//                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date " + new SimpleDateFormat("dd-MM-yyyy").format(dernierAbsence));

        if (dateDebut.after(dateFin))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La date de début ne peut pas être aprés la date de fin");


        Absence absence = Absence.builder()
            .dateDebut(dateDebut)
            .dateFin(dateFin)
            .type(type)
            .salarie(salarie)
            .build();

        if (justificatif != null) {
            String filename = uploadService.uploadJustificatif(justificatif);
            absence.setJustificatif(filename);
        }
        absenceRepository.save(absence);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Enregistrement d'un absence du salarié " + salarie.getUser().getFullname() + " pour la date " + new SimpleDateFormat("dd-MM-yyyy").format(absence.getDateDebut()))
                .service("Gestion des RH - Gestion des absences")
                .user(authService.getCurrentUser())
                .scope(Role.RH)
                .build()
        );

        List<User> receivers = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE).stream()
            .filter(user -> !user.equals(authService.getCurrentUser()))
            .collect(Collectors.toList());


        Notification notification = Notification.builder()
            .content(String.format("%s a enregistré une absence pour le salarié %s" , authService.getCurrentUser().getFullname(), salarie.getUser().getFullname()))
            .build();


        notificationService.send(notification, receivers.toArray(new User[receivers.size()]));

        Notification notificationToSalarie = Notification.builder()
            .content(String.format("Une nouvelle absence a été enregistrée. Voir la liste de vos absences pour plus de détails."))
            .build();

        notificationService.send(notificationToSalarie, absence.getSalarie().getUser());
        log.info("Notification sent to salarie");

        return absence;

    }

//    public

    @PutMapping("/{id}/{avis}")
    public Absence accepter(@PathVariable Long id, @PathVariable String avis) {
        Absence absence = absenceRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (avis.equals("accepter")) {
            absence.setAccepted(true);
        } else
            absence.setAccepted(false);
        absenceRepository.save(absence);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement(avis.equals("accepter")? "Acceptation ":"Refus " + " de la justification de absence de " + absence.getSalarie().getUser().getFullname() + " de la date " + new SimpleDateFormat("dd/MM/yyyy").format(absence.getDateDebut()))
                .service("Gestion des RH - Gestion des absences")
                .user(authService.getCurrentUser())
                .scope(Role.RH)
                .build()
        );

        List<User> receivers = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE).stream()
            .filter(user -> !user.equals(authService.getCurrentUser()))
            .collect(Collectors.toList());



        Notification notification = Notification.builder()
            .content(String.format("La justification de l'absence de \"%s\" est %s par %s" , absence.getSalarie().getUser().getFullname(), avis.equals("accepter")? "acceptée":"refusée", authService.getCurrentUser().getFullname()))
            .build();


        notificationService.send(notification, receivers.toArray(new User[receivers.size()]));

        log.info("Preparing notification for agents");
        Notification notificationToSalarie = Notification.builder()
            .content(String.format("La justification de votre absence est %s par %s" , absence.getSalarie().getUser().getFullname(), avis.equals("accepter")? "acceptée":"refusée", authService.getCurrentUser().getFullname()))
            .build();

        notificationService.send(notificationToSalarie, absence.getSalarie().getUser());
        log.info("Notification sent to salarie");

        return absence;
    }


    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteAbsence(@PathVariable Long id) {
        Absence absence = absenceRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource introuvable")
        );
        absenceRepository.deleteById(id);

        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Suppression de l'absence de " + absence.getSalarie().getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok("");
    }

}

