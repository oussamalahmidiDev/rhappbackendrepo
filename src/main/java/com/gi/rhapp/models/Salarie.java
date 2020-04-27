package com.gi.rhapp.models;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class Salarie  {


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

    @OneToOne(mappedBy = "salarie")
    private Direction direction;

    @OneToOne(mappedBy = "salarie")
    private Service service ;

    @OneToMany(mappedBy = "absence", fetch = FetchType.LAZY)
    private Absence absence;

    @OneToOne(mappedBy = "salarie")
    private sPoste poste;

    @OneToOne(mappedBy = "salarie")
    private User user;

    @OneToOne(mappedBy = "salarie")
    private Conge conge;

    @OneToOne(mappedBy = "salarie")
    private Retraite retraite;

    @OneToMany(mappedBy = "avantageNat", fetch = FetchType.LAZY)
    private AvantageNat avantageNat;
}
