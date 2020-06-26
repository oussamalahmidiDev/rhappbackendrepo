package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.NotificationService;
import com.gi.rhapp.utilities.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rh/api/conges")
@CrossOrigin("*")
@Log4j2
public class RhCongesController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private CongeRepository congeRepository;

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
    private TypeCongeRepository typeCongeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ParametresRepository parametresRepository;

    private String service = "Gestion des RH - Gestion des demandes de congés";

    @GetMapping()
    public List<Conge> getConges(){
        return congeRepository.findAllByOrderByDateCreationDesc();
    }

    @GetMapping("/parametres")
    public Parametres getParametres() {
        return parametresRepository.findById(1L).get();
    }

    @PostMapping("/parametres")
    public Parametres setParametres(@RequestBody Parametres parametres) {
        Parametres currentParametres = parametresRepository.getOne(1L);
        if (currentParametres.getCoeffConge() != null && currentParametres.getCoeffConge() >= 1.5)
            currentParametres.setCoeffConge(parametres.getCoeffConge());
        return parametresRepository.save(currentParametres);
    }

    @PostMapping("/{id}/repondre")
    public Conge repondreConge(@PathVariable("id") Long id, @RequestBody CongeReponseRequest request) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!request.getEtat().equals("ACCEPTED") && !request.getEtat().equals("REJECTED"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        conge.setEtat(EtatConge.valueOf(request.getEtat()));
        conge.setReponse(request.getReponse());

        Notification notificationToSalarie = Notification.builder()
            .content(String.format("%s a repondu à votre demande de congé."))
            .build();

        notificationService.send(notificationToSalarie, conge.getSalarie().getUser());
        log.info("Notification sent to salarie");

        return congeRepository.save(conge);
    }

    @PostMapping("create_maladie")
    public Conge createCongeMaladie (@RequestBody CongeMaladieRequest request) {
        Salarie salarie = salarieRepository.findById(request.getSalarieId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        if (request.getDateDebut().before(salarie.getDateRecrutement()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date de recrutement (" +
                new SimpleDateFormat("dd-MM-yyyy").format(salarie.getDateRecrutement()) + ")");

        salarie.getAbsences().forEach(absence -> {
            if (request.getDateDebut().before(absence.getDateFin()) && request.getDateDebut().after(absence.getDateDebut()) || (request.getDateDebut().before(absence.getDateDebut()) && request.getDateFin().after(absence.getDateFin())))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce qu'il y a déjà une absence enregsitrée durant cette période " +
                    "(de " + new SimpleDateFormat("dd-MM-yyyy").format(absence.getDateDebut()) + " jusqu'à " + new SimpleDateFormat("dd-MM-yyyy").format(absence.getDateFin()) + ")");
        });

        salarie.getConges().forEach(conge -> {
            if (!conge.getEtat().equals(EtatConge.REJECTED) && !conge.getEtat().equals(EtatConge.PENDING_RESPONSE) && request.getDateDebut().before(conge.getDateFin()) && request.getDateDebut().after(conge.getDateDebut()) || (request.getDateDebut().before(conge.getDateDebut()) && request.getDateFin().after(conge.getDateFin())))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce que le salarié était en congé durant cette période " +
                    "(de " + new SimpleDateFormat("dd-MM-yyyy").format(conge.getDateDebut()) + " jusqu'à " + new SimpleDateFormat("dd-MM-yyyy").format(conge.getDateFin()) + ")");
        });

        TypeConge maladie = typeCongeRepository.findFirstByTypeConge("MALADIE");
        if (maladie == null)
            maladie = typeCongeRepository.save(TypeConge.builder().typeConge("MALADIE").build());



        Conge conge = Conge.builder()
            .dateDebut(request.getDateDebut())
            .dateFin(request.getDateFin())
            .etat(EtatConge.ACCEPTED)
            .motif(request.getMotif())
            .salarie(salarie)
            .type(maladie)
            .build();

        return congeRepository.save(conge);
    }
    @PutMapping("/{id}/modifier")
    public Conge modifierConge(@PathVariable Long id, @RequestBody  CongeMaladieRequest request) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!conge.getType().getTypeConge().equals("MALADIE"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);

        conge.setDateDebut(request.getDateDebut());
        conge.setDateFin(request.getDateFin());
        conge.setMotif(request.getMotif());

        return congeRepository.save(conge);
    }

    @PutMapping("/{id}/declarer_retour")
    public Conge declarerRetour(@PathVariable(value = "id") Long id) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        if (!conge.getEtat().equals(EtatConge.ACCEPTED) )
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Ce congé n'est pas accepté.");

        if (conge.getDateFin().after(new Date()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Ce congé n'a pas encore achevé.");

        conge.setEtat(EtatConge.ARCHIVED);
        conge.setDateRetour(new Date());

        int jours = DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(conge.getDateFin()), new DateTime(conge.getDateRetour()));
        log.info("Jours entre date de fin et date de retour : {}", jours);
        if (jours > 0) {
            Absence absence = Absence.builder()
                .dateDebut(conge.getDateFin())
                .dateFin(conge.getDateRetour())
                .salarie(conge.getSalarie())
                .type("Retard de déclaration de retour")
                .build();

            absenceRepository.save(absence);

            Notification notification = Notification.builder()
                .content("Une absence a été enregistré à cause de retard de déclaration de retour.")
                .build();

            notificationService.send(notification, conge.getSalarie().getUser());
        }

        congeRepository.save(conge);

        activityRepository.save(
            Activity.builder()
                .evenement("Déclaration de retour de salarié " + conge.getSalarie().getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        Notification notification = Notification.builder()
            .content(String.format("L'agent %s a declaré votre retour du congé", authService.getCurrentUser().getFullname()))
            .build();

        notificationService.send(notification, conge.getSalarie().getUser());

        return conge;
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteConge(@PathVariable Long id) {
        Conge conge = congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        if (!conge.getEtat().equals(EtatConge.ARCHIVED) || !conge.getType().getTypeConge().equals("MALADIE")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Impossible de supprimer cette demande de congé à l'instant");
        }
        congeRepository.deleteById(id);

        activityRepository.save(
            Activity.builder()
                .evenement("Suppression de la demande de congé de : " + conge.getSalarie().getUser().getFullname())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok("");
    }
}
