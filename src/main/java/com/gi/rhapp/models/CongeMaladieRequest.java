package com.gi.rhapp.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class CongeMaladieRequest {

    private Long salarieId;
    private String motif;

    private LocalDate dateDebut;
    private LocalDate dateFin;

}
