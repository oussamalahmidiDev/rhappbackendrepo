package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rh/api/postes")
@CrossOrigin("*")
@Log4j2
public class RhPostesController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PosteRepository posteRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private DirectionRepository directionRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private NotificationRepository notificationRepository;

//    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthService authService;

    private String service = "Gestion des RH - Gestion de postes";

    @GetMapping()
    public List<Poste> getPostes() {
        return posteRepository.findAllByOrderByDateCreationDesc();
    }

    @PostMapping("/create")
    public Poste createPoste(@RequestBody Poste poste) {
        Service service = poste.getService();
        if (service.getId() == null) {
            service = serviceRepository.save(service);
            activityRepository.save(
                Activity.builder()
                    .evenement("Création d'un nouveau service : " + service.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        Direction direction = poste.getDirection();
        if (direction.getId() == null) {
            direction = directionRepository.save(direction);
            activityRepository.save(
                Activity.builder()
                    .evenement("Création d'une nouvelle direction : " + direction.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }
        poste = posteRepository.save(poste);
        activityRepository.save(
            Activity.builder()
                .evenement("Création d'une nouveau poste " + poste.getNom() + " dans le service de " + service.getNom())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
//        Notification notification = Notification.builder()
//            .content(String.format("L'agent %s a crée un nouveau poste %s dans le service de %s", authService.getCurrentUser().getFullname(), poste.getNom(), poste.getService().getNom()))
//            .from(authService.getCurrentUser())
//            .to(userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE).stream().filter(user -> user.getId() != authService.getCurrentUser().getId()).collect(Collectors.toList()))
//            .build();

//        notificationService.publish(notificationRepository.save(notification));


        return poste;
    }

    @PostMapping("/{id}/affecter")
    public Poste affecterSalarie(@PathVariable("id") Long id, @RequestBody AffectationRequest request) {
        Poste poste = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );
        Salarie selectedSalarie = salarieRepository.findById(request.getSalarieId()).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarie introuvable")
        );

        // check if salarie is already affected to another poste, if it's the case it will be overwritten
        if (selectedSalarie.getPoste() != null)
            selectedSalarie.getPoste().setSalarie(null);

        selectedSalarie.setFonction(request.getFonctions());
        selectedSalarie.setDateAffectation(new Date());

        poste.setSalarie(selectedSalarie);

        poste = posteRepository.save(poste);
        activityRepository.save(
            Activity.builder()
                .evenement("Affectation de " + selectedSalarie.getUser().getFullname() + " au poste de " + poste.getNom())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        List<User> receiver = new ArrayList<>();
        receiver.add(selectedSalarie.getUser());

        List<User> otherAgents = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE);

        Notification notificationToAgents = Notification.builder()
            .content(String.format("Le salarié \"%s\" été affecté au poste de \"%s\" (service de %s)", selectedSalarie.getUser().getFullname(), poste.getNom(), poste.getService().getNom()))
            .to(otherAgents)
            .build();

        notificationRepository.save(notificationToAgents);

        Notification notificationToSalarie = Notification.builder()
            .content(String.format("Vous avez été affecté au poste de \"%s\" (service de %s)", poste.getNom(), poste.getService().getNom()))
            .to(receiver)
            .build();

        notificationRepository.save(notificationToSalarie);

        return poste;
    }

    @PutMapping("/{id}/salarie/supprimer")
    public Poste deleteSalarie(@PathVariable("id") Long id) {
        Poste poste = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );
        Salarie salarie = poste.getSalarie();
        salarie.setFonction(null);
        poste.setSalarie(null);
        poste = posteRepository.save(poste);
        activityRepository.save(
            Activity.builder()
                .evenement("Deaffectation de " + salarie.getUser().getFullname() + " du poste de " + poste.getNom())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );

        List<User> receiver = new ArrayList<>();
        receiver.add(salarie.getUser());

        List<User> otherAgents = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE);

        Notification notificationToAgents = Notification.builder()
            .content(String.format("Le salarié \"%s\" été déaffecté du poste de \"%s\" (service de %s)", salarie.getUser().getFullname(), poste.getNom(), poste.getService().getNom()))
            .to(otherAgents)
            .build();

        notificationRepository.save(notificationToAgents);

        Notification notificationToSalarie = Notification.builder()
            .content(String.format("Vous avez été déaffecté du poste de \"%s\" (service de %s)", poste.getNom(), poste.getService().getNom()))
            .to(receiver)
            .build();

        notificationRepository.save(notificationToSalarie);
        return poste;
    }

    @PutMapping("/{id}/modifier")
    public Poste modifierPoste(@PathVariable Long id, @RequestBody Poste poste) {
        Poste posteFromDB = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );

        Service service = poste.getService();
        if (service.getId() == null) {
            service = serviceRepository.save(service);
            activityRepository.save(
                Activity.builder()
                    .evenement("Création d'un nouveau service : " + service.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }

        Direction direction = poste.getDirection();
        if (direction.getId() == null) {
            direction = directionRepository.save(direction);
            activityRepository.save(
                Activity.builder()
                    .evenement("Création d'une nouvelle direction : " + direction.getNom())
                    .service(this.service)
                    .user(authService.getCurrentUser())
                    .scope(Role.ADMIN)
                    .build()
            );
        }
        posteFromDB.setNom(poste.getNom());
        posteFromDB.setCompetences(poste.getCompetences());
        posteFromDB.setDirection(direction);
        posteFromDB.setService(service);
        posteFromDB.setDivision(poste.getDivision());
        posteRepository.save(posteFromDB);
        activityRepository.save(
            Activity.builder()
                .evenement("Modification des informations du poste de " + poste.getNom())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return posteFromDB;
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deletePoste(@PathVariable Long id) {
        Poste poste = posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable")
        );

//        poste.setSalarie(null);
//        posteRepository.saveAndFlush(poste);
//        posteRepository.
        posteRepository.deleteById(id);
//        posteRepository.
//        posteRepository.flush();

//        if (posteRepository.findById(id).isPresent())
//            return deletePoste(id);

        activityRepository.save(
            Activity.builder()
                .evenement("Suppression de poste de " + poste.getNom())
                .service(this.service)
                .user(authService.getCurrentUser())
                .scope(Role.ADMIN)
                .build()
        );
        return ResponseEntity.ok("");
    }
}
