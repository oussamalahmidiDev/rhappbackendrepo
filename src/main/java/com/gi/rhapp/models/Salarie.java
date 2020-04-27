package com.gi.rhapp.models;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Salarie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private Long numSomme;
    private String nom , prenom , telephone, adresse;
    private Date dateNaissance ;
    private String lieuNaissance ;


    private Date dateAffectation;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateUpdate;

    private String diplomeObt;

    private String fonction;

    private String cinUrg , nomUrg  , prenomUrg , adresseUrg , emailUrg ;
    private Long solde;

    @ManyToOne
    private Direction direction;

    @ManyToOne
    private Service service;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY)
    private List<Absence> absences;

    @OneToOne(mappedBy = "salarie")
    private Poste poste;

    @OneToOne
    private User user;

    @OneToMany(mappedBy = "salarie")
    private List<Conge> conges;

    @OneToOne(mappedBy = "salarie")
    private Retraite retraite;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY)
    private List<AvantageNat> avantages;
}
