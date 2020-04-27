package com.gi.rhapp.models;

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
public class Absence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dateDebut, dateFin;

    private String justificatif;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

//    a verifier
    private String type;

    @ManyToOne
    private Salarie salarie;
}
