package com.gi.rhapp.models;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Date;

@Data
public class SalarieRequest {

    @NotNull
    private Direction direction;
    @NotNull
    private Service service;
    @NotNull
    private String numSomme;

    @NotNull
    private String nom;
    @NotNull
    private String prenom;
    @NotNull
    private String email;

    private Date dateNaissance;
    private LocalDate dateRecrutement;
}
