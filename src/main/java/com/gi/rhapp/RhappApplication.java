package com.gi.rhapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@SpringBootApplication
@RestController
public class RhappApplication {

    public static void main(String[] args) {
        SpringApplication.run(RhappApplication.class, args);
    }

    @GetMapping("/")
    public String helloWorld() {
        return "Hello From RH APP from HEROKU";
    }
}
