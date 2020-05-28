package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;
import javax.validation.constraints.Email;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class Salarie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(unique = true)
    private String numSomme;

    @Column(unique = true)
    private String cin ;

    private int joursDisponible ;


//    private double salaire;

    private String  adresse;
    private String cv;

    private Date dateNaissance ;
    private String lieuNaissance ;


    private Date dateAffectation;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateUpdate;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @JsonIgnoreProperties(value = {"salarie"})
    private Collection<Diplome> diplomeObt;

    private String fonction;

    private String etatFamiliale;

    private int nmbEnf;

    private String cinUrg , nomUrg  , prenomUrg , adresseUrg , telephoneUrg;

    @Email
    @Column(nullable = true)
    private String emailUrg ;
    private Long solde;

    @ManyToOne
    @JsonIgnoreProperties({"salaries", "postes"})
    private Direction direction;

    @ManyToOne
    @JsonIgnoreProperties({"salaries", "postes"})
    private Service service;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY,  cascade=CascadeType.ALL )
    @JsonIgnoreProperties({"salarie"})
    private List<Absence> absences;

    @OneToOne(mappedBy = "salarie", fetch = FetchType.LAZY , cascade=CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private Poste poste;

    @OneToOne
    @JsonIgnoreProperties({"salarie", "id", "dateCreation", "dateModification"})
    @JsonUnwrapped
    private User user;

    @OneToMany(mappedBy = "salarie",  cascade=CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private List<Conge> conges;

    @OneToOne(mappedBy = "salarie", fetch = FetchType.LAZY ,  cascade=CascadeType.ALL)
    @JsonIgnoreProperties(value = {"salarie"}, allowSetters = true)
    private Retraite retraite;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY , cascade=CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private Collection<AvantageNat> avantages;

    @PrePersist
    void initialStat(){
        joursDisponible =18;
    }

}
