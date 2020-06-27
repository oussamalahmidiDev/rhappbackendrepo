package com.gi.rhapp.models;

import lombok.Data;

import java.util.Date;

@Data
public class SalarieProfileRequest {

    private String lieuNaissance;
    private Date dateNaissance;
    private String adresse;
    private String etatFamiliale;
    private int nmbEnf;
    private String cinUrg;
    private String adresseUrg;
    private String emailUrg;
    private String nomUrg;
    private String prenomUrg;
    private String telephoneUrg;

}
