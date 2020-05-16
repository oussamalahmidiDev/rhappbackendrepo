package com.gi.rhapp.models;

import com.gi.rhapp.enumerations.EtatConge;
import lombok.Data;

@Data
public class CongeReponseRequest {

    private String etat;
    private String reponse;
}
