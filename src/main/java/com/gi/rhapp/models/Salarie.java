package com.gi.rhapp.models;

import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;


import java.io.Serializable;
import javax.persistence.*;

import java.util.Date;

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

    private Direction direction;
    private Service service ;
}
