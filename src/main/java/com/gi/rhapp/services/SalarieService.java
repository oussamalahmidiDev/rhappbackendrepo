package com.gi.rhapp.services;


import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.enumerations.EtatRetraite;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.utilities.BeanUtils;
import com.gi.rhapp.utilities.DateUtils;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PostLoad;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Log4j2
@Service
public class SalarieService {

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private TypeRetraiteRepository typeRetraiteRepository;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void addProperties(Salarie salarie) {
        log.info("Calcul de jours de travail");
        try {
            Date dateNaissance = salarie.getDateNaissance();
            Date dateRecrutement = salarie.getDateRecrutement();

            log.info("Salarie : {}", salarie.getUser().getFullname());
            log.info("Date naissance : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateNaissance));
            log.info("Date recr : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateRecrutement));
            int ageApresSixMois = Period.between(dateNaissance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now().plusMonths(6)).getYears();
            log.info("Age apres 6 mois : {}", ageApresSixMois);
            log.info("------");
//            Plannifier une retraite apres 6 moix
            if (ageApresSixMois >= 60 && salarie.getRetraite() == null) {
                genererRetraite(salarie);
            }
//            Enregistrer une retraite si le salarie a depassé 60 ans
            int age = Period.between(dateNaissance.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), LocalDate.now()).getYears();
            if (age >= 60 && salarie.getRetraite().getEtat().equals(EtatRetraite.SCHEDULED)) {
                log.info("Changement de l'etat de la retraite");
                Retraite retraite = salarie.getRetraite();
                retraite.setEtat(EtatRetraite.PENDING_RT_AVTG);
                retraiteRepository.save(retraite);

                List<User> receiver = new ArrayList();
                receiver.add(salarie.getUser());

//                Notifier au salarié
                notificationRepository.save(Notification.builder()
                    .content("Votre retraite est desormais active depuis aujourd'hui. En attendant les agents RH valide votre retraite.")
                    .to(receiver)
                    .build()
                );

//                Notifier les agents
                notificationRepository.save(Notification.builder()
                    .content("La retraite de " + salarie.getUser().getFullname() + " est desormais active. Veuillez continuer le processus de la validation.")
                    .to(userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE))
                    .build()
                );
            }
            int nombreJoursTravail = DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(dateRecrutement), DateTime.now());
            log.info("Nombre jours travail : {}", nombreJoursTravail);

            int nombreJoursAbsence = salarie.getAbsences().stream()
                .mapToInt(absence -> DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(absence.getDateDebut()), new DateTime(absence.getDateFin()))).sum();
            log.info("nombre de jours d'absence : {}", nombreJoursAbsence);

            int nombreJoursConge = salarie.getConges().stream()
                .filter(conge -> conge.getEtat().equals(EtatConge.ACCEPTED) || conge.getEtat().equals(EtatConge.ARCHIVED))
                .mapToInt(conge -> DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(conge.getDateDebut()), new DateTime(conge.getDateFin()))).sum();
            log.info("nombre de jours de conges : {}", nombreJoursConge);

            log.info("Nombre jours sans jours d'absence ou conge : {}", nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge));
            int mois = ((nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge)) / 30) + 1;

            log.info("Nombre mois travail : {}", mois);

            if (mois > 6) {
                log.info("Autorisé au congé");
            } else {
                log.info("n'est pas Autorisé au congé");
            }

            salarie.setProperties(new HashMap<>());
            salarie.add("jours_travail", nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge));
            salarie.add("mois_travail", mois);
            salarie.add("jours_absence", nombreJoursAbsence);
            salarie.add("jours_conge", nombreJoursConge);

        } catch (NullPointerException e) {
            log.info("Throws a null pointer exception : {}", e.getMessage());
        }
    }

    void genererRetraite(Salarie salarie) {
        log.info("Enregistrement d'une retraite");
        Retraite retraite = new Retraite();
        retraite.setSalarie(salarie);
        retraite.setReference("REF" + salarie.getId() + System.currentTimeMillis());
        retraite.setDateRetraite(LocalDate.now().plusMonths(6));
        log.info("Ref de retraite : {}", retraite.getReference());
        log.info("Date de retraite : {}", retraite.getDateRetraite().toString());
        if (typeRetraiteRepository.findFirstByTypeRetraite("Retraite automatique") == null)
            typeRetraiteRepository.save(new TypeRetraite("Retraite automatique"));
        retraite.setType(typeRetraiteRepository.findFirstByTypeRetraite("Retraite automatique"));
        log.info("Type de retraite : {}", retraite.getType().getTypeRetraite());
        retraiteRepository.save(retraite);

        List<User> receiver = new ArrayList();
        receiver.add(salarie.getUser());

        notificationRepository.save(Notification.builder()
            .content("Il reste moins de six mois pour votre retraite. Voir la page des retraites pour plus d'informations")
            .to(receiver)
            .build()
        );

        notificationRepository.save(Notification.builder()
            .content("Une retraite a été enregistrée automatiquement pour le salarié " + salarie.getUser().getFullname())
            .to(userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE))
            .build()
        );

        activitiesService.saveAndSend(Activity.builder()
            .evenement("Enregistrement automatique de la retraite")
            .service("Gestion des retraites")
            .user(salarie.getUser())
            .scope(Role.SALARIE)
            .build());
    }
}
