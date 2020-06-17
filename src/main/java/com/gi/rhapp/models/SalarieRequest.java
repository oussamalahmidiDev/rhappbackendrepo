package com.gi.rhapp.models;

import lombok.Data;

import java.util.Date;

@Data
public class SalarieRequest {

    private Direction direction;
    private Service service;
    private String numSomme;

    private String nom;
    private String prenom;
    private String email;

    private Long solde;

    private Date dateNaissance;
    private Date dateRecrutement;
}
