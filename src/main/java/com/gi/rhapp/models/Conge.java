package com.gi.rhapp.models;

import com.gi.rhapp.enumerations.EtatConge;
import lombok.AllArgsConstructor;
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
public class Conge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut, DateFin, DateRetour;

    private int duree;

    private String motif;

    @Enumerated(EnumType.STRING)
    private EtatConge etat;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

    @ManyToOne
    private TypeConge type;

    @ManyToOne
    private Salarie salarie;



}
