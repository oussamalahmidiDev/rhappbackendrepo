package com.gi.rhapp.models;

import lombok.Data;

import java.util.Date;

@Data
public class CongeMaladieRequest {

    private Long salarieId;
    private String motif;

    private Date dateDebut;
    private Date dateFin;

}
