package com.gi.rhapp;

import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.utilities.DateUtils;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.base.BaseDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static org.joda.time.DateTimeConstants.SATURDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;


@SpringBootApplication
@RestController
@Builder
@Log4j2
public class RhappApplication implements CommandLineRunner {

    @Autowired
    private SalarieRepository salarieRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private DirectionRepository directionRepository;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private PosteRepository posteRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvantageNatRepository avantageNatRepository;

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private ParametresRepository parametresRepository;


    public static void main(String[] args) {

        SpringApplication.run(RhappApplication.class, args);
    }

    @GetMapping("/")
    public String helloWorld() {
        template.convertAndSendToUser("lahmidioussama14@gmail.com", "/topic/notifications", "Hello fromWS");
        return "Hello From RH APP from HEROKU v2";
    }

    @GetMapping("/test/salarie_states/{id}")
    public void salarieStates(@PathVariable Long id) {
        Salarie salarie = salarieRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Date dateNaissance = salarie.getDateNaissance();
        Date dateRecrutement = salarie.getDateRecrutement();
        log.info("Date naissance : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateNaissance));
        log.info("Date recr : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateRecrutement));

        log.info("------");

        int nombreJoursTravail = DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(dateRecrutement), DateTime.now());
        log.info("Nombre jours travail : {}", nombreJoursTravail);

        int nombreJoursAbsence = salarie.getAbsences().stream()
            .filter(absence -> !absence.getAccepted())
            .mapToInt(absence -> DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(absence.getDateDebut()), new DateTime(absence.getDateFin()))).sum();

        log.info("nombre de jours d'absence : {}", nombreJoursAbsence);
        log.info("Nombre jours sans jours d'absence : {}", nombreJoursTravail - nombreJoursAbsence);
        int mois = ((nombreJoursTravail - nombreJoursAbsence) / 30) + 1;

        log.info("Nombre mois travail : {}", mois);

        if (mois > 6) {
            log.info("Autorisé au congé");
        } else {
            log.info("n'est pas Autorisé au congé");
        }
    }




    //just for test
    @Override
    public void run(String... args) throws Exception {
//        if (parametresRepository.getOne(1L).getId() == null) {
//
//        }
        Parametres parametres = new Parametres();
        parametres.setCoeffConge(1.5);
        parametres.setId(1L);
        parametresRepository.save(parametres);
    }
}
