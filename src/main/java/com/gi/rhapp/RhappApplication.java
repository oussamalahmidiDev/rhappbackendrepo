package com.gi.rhapp;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Retraite;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Date;

@SpringBootApplication
@RestController
public class RhappApplication implements CommandLineRunner {

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

//        Salarie salarie = salarieRepository.save(new Salarie("EE952974","KHALIL","DAOULAT",new Date(), "INDIA" , "MOL CHI"));
//        Salarie salarie2 = salarieRepository.save(new Salarie("EE958974","OUSSAMA","LAHMIDI",new Date(), "INDIA" , "MOL CHI"));
//        Salarie salarie3 = salarieRepository.save(new Salarie("EE986974","NOUHAILA","BOUZITI",new Date(), "INDIA" , "MOL CHI"));
//        Salarie salarie4 = salarieRepository.save(new Salarie("EE902974","testos","fakhr",new Date(), "INDIA" , "MOL CHI"));
//
//
//        Retraite retraite = retraiteRepository.save(new Retraite(new Date() , new Date() , salarie4));
//
//        Absence absence = absenceRepository.save(new Absence(new Date() , new Date() , salarie2));
//        Absence absence1 = absenceRepository.save(new Absence(new Date() , new Date() , salarie2));

    }
}
