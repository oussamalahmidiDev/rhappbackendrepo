package com.gi.rhapp.models;

import java.io.Serializable;
import java.util.Date;

public class Salarie implements Serializable {

    private Long id ;
    private Long numSomme;
    private String nom , prenom , telephone, adresse;
    private Date DateNaissance ;
    private String lieuNaissance ;
    private Date DateAffectation;

    private String diplomeObt;
    private Direction direction;
    private Service service ;
    private String fonction;

    private String cinUrg , nomUrg  , prenomUrg , adresseUrg , emailUrg ;
    private Long solde;
}
