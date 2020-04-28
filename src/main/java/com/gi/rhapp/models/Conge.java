package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gi.rhapp.enumerations.EtatConge;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Conge  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut;

    private Date DateFin;

    private Date DateRetour;

    private int duree;

    private String motif;

    @Enumerated(EnumType.STRING)
    private EtatConge etat;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

    @ManyToOne
    @JsonIgnoreProperties({"conges"})
    private TypeConge type;

    @ManyToOne
    @JsonIgnoreProperties({"conges","absences","avantages"})
    private Salarie salarie;



}
