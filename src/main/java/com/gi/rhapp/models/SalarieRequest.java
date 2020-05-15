package com.gi.rhapp.models;

import lombok.Data;

@Data
public class SalarieRequest {

    private Direction direction;
    private Service service;
    private String numSomme;

    private String nom;
    private String prenom;
    private String email;

    private Long solde;
}
