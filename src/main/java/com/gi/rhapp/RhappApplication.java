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

    }
}
