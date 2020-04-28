package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class Absence  {


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
    @JsonIgnoreProperties({"conges","absences","avantages"})
    private Salarie salarie;

    public Absence(Date date, Date date1, Salarie salarie) {
        this.dateDebut=date;
        this.dateFin=date1;
        this.salarie=salarie;
    }
}
