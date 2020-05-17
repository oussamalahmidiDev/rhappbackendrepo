package com.gi.rhapp;

import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Date;

@SpringBootApplication
@RestController
@Builder
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


    public static void main(String[] args) {

        SpringApplication.run(RhappApplication.class, args);
    }

    @GetMapping("/")
    public String helloWorld() {
        return "Hello From RH APP from HEROKU v2";
    }


    //just for test
    @Override
    public void run(String... args) throws Exception {

        // Service service = serviceRepository.save(Service.builder().nom("Dev service").build());
        // Direction direction = directionRepository.save(Direction.builder().nom("Soft Direction").build());


        // User khalil = userRepository.save(User.builder()
        //         .nom("DAOULAT")
        //         .prenom("KHALIL")
        //         .password("khalil")
        //         .telephone("066666666")
        //         .email("daoulat.khalil@gmail.com")
        //         .build());

        // User oussama = usessrRepository.save(User.builder()
        //         .nom("LAHMIDI")
        //         .prenom("OUSSAMA")
        //         .email("oussama@gmail.com")
        //         .build());

        // User nouhaila = userRepository.save(User.builder()
        //         .nom("BOUZITI")
        //         .prenom("NOUHAILA")
        //         .email("nouhaila@gmail.com")
        //         .build());

        // User nachrha = userRepository.save(User.builder()
        //         .nom("TESTOS")
        //         .prenom("HAMID")
        //         .email("hamid@gmail.com")
        //         .build());

        // Salarie salarie1 = salarieRepository.save(Salarie.builder()
        //         .dateNaissance(new Date())
        //         .lieuNaissance("Marrakech")
        //         .fonction("Engineer")
        //         .solde(25L)
        //         .etatFamiliale("celibataire")
        //         .numSomme(8540479L)
        //         .cin("EE8541256")
        //         .nomUrg("nomUrg")
        //         .prenomUrg("prenomUrg")
        //         .emailUrg("emailUrg@urg.com")
        //         .telephoneUrg("0625458745")
        //         .cinUrg("EE7845966")
        //         .adresseUrg("adressUrg")
        //         .service(service)
        //         .direction(direction)
        //         .user(khalil)
        //         .build());

        // Salarie salarie2 = salarieRepository.save(Salarie.builder()
        //         .fonction("RH")
        //         .service(service)
        //         .direction(direction)
        //         .user(oussama)
        //         .build());

        // Salarie salarie3 = salarieRepository.save(Salarie.builder()
        //         .fonction("Engineer")
        //         .service(service)
        //         .direction(direction)
        //         .user(nouhaila)
        //         .build());

        // Salarie salarie4 = salarieRepository.save(Salarie.builder()
        //         .fonction("BERGAG")
        //         .service(service)
        //         .direction(direction)
        //         .user(nachrha)
        //         .build());

        // absenceRepository.save(Absence.builder().salarie(salarie1).dateDebut(new Date()).dateFin(new Date()).build());
        // retraiteRepository.save(Retraite.builder().salarie(salarie1).dateRetraite(new Date()).dateValidation(new Date()).remarques("test").build());
        // avantageNatRepository.save(AvantageNat.builder().salarie(salarie1).commission("commission").build());
        // TypeConge type1 = typeCongeRepository.save(TypeConge.builder().typeConge("NORMALE").build());

        // congeRepository.save(Conge.builder()
        //         .motif("Repos")
        //         .type(type1)
        //         .salarie(salarie1)
        //         .dateCreation(new Date())
        //         .dateDebut(new Date())
        //         .dateFin(new Date())
        //         .build());

        // Poste software_engineer1 = posteRepository.save(Poste.builder().salarie(salarie1).nom("Software Engineer").build());
        // Poste software_engineer2 = posteRepository.save(Poste.builder().salarie(salarie3).nom("Software Engineer").build());
        // Poste software_engineer3 = posteRepository.save(Poste.builder().salarie(salarie4).nom("Software Engineer").build());
        // Diplome diplome1 = diplomeRepository.save(Diplome.builder().salarie(salarie1).name("Engineer").dateDiplome(new Date()).build());
        // Poste rh = posteRepository.save(Poste.builder().salarie(salarie2).nom("Resource human").build());


    }
}
