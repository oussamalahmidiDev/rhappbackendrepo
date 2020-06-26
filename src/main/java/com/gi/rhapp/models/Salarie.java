package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.repositories.RetraiteRepository;
import com.gi.rhapp.repositories.TypeRetraiteRepository;
import com.gi.rhapp.services.ActivitiesService;
import com.gi.rhapp.services.SalarieService;
import com.gi.rhapp.utilities.DateUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.stereotype.Component;


import javax.persistence.*;
import javax.validation.constraints.Email;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Log4j2
@EntityListeners(SalarieListener.class)
public class Salarie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted;

    private String raisonSuppression;

    @Column(unique = true)
    private String numSomme;

    @Column(unique = true)
    private String cin;

//    private int joursDisponible;


//    private double salaire;

    private String adresse;
    private String cv;

    private Date dateNaissance;
    private LocalDate dateRecrutement;

    private String lieuNaissance;


    private Date dateAffectation;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateUpdate;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"salarie"})
    private Collection<Diplome> diplomeObt;

    private String fonction;

    private String etatFamiliale;

    private int nmbEnf;

    private String cinUrg, nomUrg, prenomUrg, adresseUrg, telephoneUrg;

    @Email
    private String emailUrg;

    @ManyToOne
    @JsonIgnoreProperties({"salaries", "postes"})
    private Direction direction;

    @ManyToOne
    @JsonIgnoreProperties({"salaries", "postes"})
    private Service service;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private List<Absence> absences;

    @OneToOne(mappedBy = "salarie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private Poste poste;

    @OneToOne
    @JsonIgnoreProperties({"salarie", "id", "dateCreation", "dateModification"})
    @JsonUnwrapped
    private User user;

    @OneToMany(mappedBy = "salarie", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private List<Conge> conges;

    @OneToOne(mappedBy = "salarie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = {"salarie"}, allowSetters = true)
    private Retraite retraite;

    @OneToMany(mappedBy = "salarie", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"salarie"})
    private Collection<AvantageNat> avantages;

    @PrePersist
    void initialStat() {
//        joursDisponible = 18;
        deleted = false;
    }

    @Transient
    @JsonUnwrapped
    private Map<String, Object> properties;

    @JsonAnySetter
    public void add(String key, Object value) {
        properties.put(key, value);
    }

}

@Component
@Log4j2
class SalarieListener {

    static private SalarieService service;

    @Autowired
    public void init(SalarieService service) {
        SalarieListener.service = service;
        log.info("Initializing with dependency [" + service + "]");
    }
//
    @PostLoad
    public void onPostLoad(Salarie salarie) {

        service.addProperties(salarie);
    }
}

