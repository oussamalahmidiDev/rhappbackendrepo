package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.gi.rhapp.enumerations.EtatConge;
import com.gi.rhapp.utilities.DateUtils;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.UpdateTimestamp;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.hibernate.annotations.CreationTimestamp;


import javax.persistence.*;
import javax.validation.constraints.Email;

import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
@Log4j2
@EntityListeners(SalarieListener.class)
public class Salarie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(columnDefinition = "boolean default false")
    private Boolean deleted;

    private String raisonSuppression;

    @Column(unique = true)
    private String numSomme;

    @Column(unique = true)
    private String cin ;

    private int joursDisponible ;


//    private double salaire;

    private String  adresse;
    private String cv;

    private Date dateNaissance ;
    private Date dateRecrutement;

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
        deleted = false;
    }

    @Transient
    @JsonUnwrapped
    private Map<String, Object> properties;

    @JsonAnySetter
    public void add(String key, Object value) {
        properties.put(key, value);
    }

//    @PostLoad
//    void onPostLoad() {
//        log.info("Calcul de jours de travail");
//
//    }

//    @
}

@Log4j2
class SalarieListener {

    @PostLoad
    void onPostLoad(Salarie salarie) {
        log.info("Calcul de jours de travail");
        try {
            Date dateNaissance = salarie.getDateNaissance();
            Date dateRecrutement = salarie.getDateRecrutement();

            log.info("Date naissance : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateNaissance));
            log.info("Date recr : {}", new SimpleDateFormat("dd-MM-yyyy").format(dateRecrutement));
            log.info("------");
            int nombreJoursTravail = DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(dateRecrutement), DateTime.now());
            log.info("Nombre jours travail : {}", nombreJoursTravail);

            int nombreJoursAbsence = salarie.getAbsences().stream()
                .mapToInt(absence -> DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(absence.getDateDebut()), new DateTime(absence.getDateFin()))).sum();
            log.info("nombre de jours d'absence : {}", nombreJoursAbsence);

            int nombreJoursConge = salarie.getConges().stream()
                .filter(conge -> conge.getEtat().equals(EtatConge.ACCEPTED) || conge.getEtat().equals(EtatConge.ARCHIVED))
                .mapToInt(conge -> DateUtils.getDaysBetweenIgnoreWeekends(new DateTime(conge.getDateDebut()), new DateTime(conge.getDateFin()))).sum();
            log.info("nombre de jours de conges : {}", nombreJoursConge);

            log.info("Nombre jours sans jours d'absence ou conge : {}", nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge));
            int mois = ((nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge)) / 30) + 1;

            log.info("Nombre mois travail : {}", mois);

            if (mois > 6) {
                log.info("Autorisé au congé");
            } else {
                log.info("n'est pas Autorisé au congé");
            }

            salarie.setProperties(new HashMap<>());
            salarie.add("jours_travail", nombreJoursTravail - (nombreJoursAbsence + nombreJoursConge));
            salarie.add("mois_travail", mois);
            salarie.add("jours_absence", nombreJoursAbsence);
            salarie.add("jours_conge", nombreJoursConge);

        } catch (NullPointerException e) {
            log.info("Throws a null pointer exception");
            return;
        }

    }
}
