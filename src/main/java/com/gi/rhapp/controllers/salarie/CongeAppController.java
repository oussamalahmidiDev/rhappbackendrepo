package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import com.gi.rhapp.utilities.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/salarie/api/conges")
@CrossOrigin("*")
@Log4j2
public class CongeAppController {

    String service = "Panneau de salarié - Demandes de congés";

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private  TypeCongeRepository typeCongeRepository;

    @Autowired
    private ProfileAppController profileAppController;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParametresRepository parametresRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }


    @GetMapping()
    public List<Conge> getConges () {
        return getProfile().getConges();
    }

    @GetMapping("/{id}")
    public Conge  getOneConge(@PathVariable(value = "id")Long id){

        return congeRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le Conge avec id = " + id + " est introuvable."));
    }

    @PostMapping("/create")
    public Conge createConge (@RequestBody CongeSalarieRequest congeRequest ) {
        Conge conge = congeRequest.getConge();
        Salarie salarie = getProfile();
        TypeConge type = typeCongeRepository.save(TypeConge.builder().typeConge(congeRequest.getTypeConge()).build());
        conge.setType(type);
        System.out.println("NOMBRE");
//        System.out.println(parametresRepository.findById(1L).get().getNombreMinJoursConge());

        if ((int)getProfile().getProperties().get("max_jours_conge") < conge.getDuree())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Vous ne pouvez pas dépasser %d jours du congé.",getProfile().getProperties().get("jours_conge")));

        if (conge.getDateDebut().isBefore(salarie.getDateRecrutement()))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date de recrutement (" +
                salarie.getDateRecrutement() + ")");

        salarie.getAbsences().forEach(absence -> {
            if (conge.getDateDebut().isBefore(absence.getDateFin()) && conge.getDateDebut().isAfter(absence.getDateDebut()) || (conge.getDateDebut().isBefore(absence.getDateDebut()) && conge.getDateFin().isAfter(absence.getDateFin())))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce qu'il y a déjà une absence enregsitrée durant cette période " +
                    "(de " + absence.getDateDebut() + " jusqu'à " + absence.getDateFin() + ")");
        });

        salarie.getConges().forEach(element -> {
            if (!element.getEtat().equals(EtatConge.REJECTED) && !element.getEtat().equals(EtatConge.PENDING_RESPONSE) && conge.getDateDebut().isBefore(element.getDateFin()) && conge.getDateDebut().isAfter(element.getDateDebut()) || (conge.getDateDebut().isBefore(element.getDateDebut()) && conge.getDateFin().isAfter(element.getDateFin())))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce que le salarié était en congé durant cette période " +
                    "(de " + conge.getDateDebut() + " jusqu'à " + conge.getDateFin() + ")");
        });
        congeRepository.save(Conge.builder()
                .salarie(getProfile())
                .type(type)
                .dateDebut(conge.getDateDebut())
                .dateFin(conge.getDateFin())
                .duree(conge.getDuree())
                .motif(conge.getMotif())
                .build());

        activityRepository.save(
                Activity.builder()
                        .evenement("Le salarié " + getProfile().getUser().getFullname() + " a ajouté une demande de congé")
                        .service(service)
                        .user(getProfile().getUser())
                        .scope(Role.RH)
                        .build()
        );

        return conge;
    }

    @PutMapping("/{id}/modifier")
    @Modifying
    public Conge modifyConge(@PathVariable(value = "id")Long id , @RequestBody CongeSalarieRequest congeRequest){
        try{
//            System.out.println(congeRequest);
            TypeConge type = typeCongeRepository.save(TypeConge.builder().typeConge(congeRequest.getTypeConge()).build());
            Conge newConge = congeRequest.getConge();
            Salarie salarie = getProfile();

            if (newConge.getDateDebut().isBefore(salarie.getDateRecrutement()))
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible de créer une absence avant la date de recrutement (" +
                    new SimpleDateFormat("dd-MM-yyyy").format(salarie.getDateRecrutement()) + ")");

            salarie.getAbsences().forEach(absence -> {
                if (newConge.getDateDebut().isBefore(absence.getDateFin()) && newConge.getDateDebut().isAfter(absence.getDateDebut()) || (newConge.getDateDebut().isBefore(absence.getDateDebut()) && newConge.getDateFin().isAfter(absence.getDateFin())))
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce qu'il y a déjà une absence enregsitrée durant cette période " +
                        "(de " + absence.getDateDebut() + " jusqu'à " + absence.getDateFin() + ")");
            });

            salarie.getConges().forEach(element -> {
                if (!element.getEtat().equals(EtatConge.REJECTED) && !element.getEtat().equals(EtatConge.PENDING_RESPONSE) && newConge.getDateDebut().isBefore(element.getDateFin()) && newConge.getDateDebut().isAfter(element.getDateDebut()) || (newConge.getDateDebut().isBefore(element.getDateDebut()) && newConge.getDateFin().isAfter(element.getDateFin())))
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Impossible d'enregistrer un congé de maladie durant cette période par ce que le salarié était en congé durant cette période " +
                        "(de " + newConge.getDateDebut() + " jusqu'à " + newConge.getDateFin() + ")");
            });
            congeRepository.save(Conge.builder()
                    .salarie(getProfile())
                    .type(type)
                    .id(newConge.getId())
                    .duree(newConge.getDuree())
                    .dateCreation(newConge.getDateCreation())
                    .etat(newConge.getEtat())
                    .dateDebut(newConge.getDateDebut())
                    .dateFin(newConge.getDateFin())
                    .motif(newConge.getMotif())
                    .build());

            activityRepository.save(
                    Activity.builder()
                            .evenement("Le salarié " + getProfile().getUser().getFullname() + " a modifié sa demande de congé")
                            .service(service)
                            .user(getProfile().getUser())
                            .scope(Role.RH)
                            .build()
            );
            return newConge;

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Le congé avec id "+id+" est introuvable");
        }

    }

    @DeleteMapping("/{id}/supprimer")
    public void  deleteConge(@PathVariable(value = "id")Long id){
        congeRepository.deleteById(id);
        activityRepository.save(
                Activity.builder()
                        .evenement("Le salarié " + getProfile().getUser().getFullname() + " a supprimé sa demande de congé")
                        .service(service)
                        .user(getProfile().getUser())
                        .scope(Role.RH)
                        .build()
        );
//        return ResponseEntity.ok("l'Absence est supprimer avec succès");
    }

    @PutMapping("/{id}/retour")
    public Conge addRetourDate(@PathVariable(value = "id")Long id , @RequestParam("dateRetour") LocalDate dateRetour){
         System.out.println(dateRetour);
         Conge conge = congeRepository.findById(id).get();
         conge.setDateRetour(dateRetour);
         conge.setEtat(EtatConge.PENDING_RESPONSE);
         return congeRepository.save(conge);

    }
}
