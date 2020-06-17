package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.*;
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

@RestController
@RequestMapping("/rh/api/absences")
@CrossOrigin("*")
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
    private NotificationRepository notificationRepository;

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

        if (dernierAbsence != null)
            if (dateDebut.before(dernierAbsence))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date " + new SimpleDateFormat("dd-MM-yyyy").format(dernierAbsence));

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

        return absenceRepository.save(absence);

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

        Notification notification = Notification.builder()
            .content(String.format("La justification de l'absence de \"%s\" est %s" , absence.getSalarie().getUser().getFullname(), avis.equals("accepter")? "acceptée":"refusée"))
            .to(userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE))
            .build();

        notificationRepository.save(notification);


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

