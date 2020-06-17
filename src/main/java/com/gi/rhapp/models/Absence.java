package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @Transient
    private String justificatifLink;

    @JsonProperty("justificatif_link")
    public String getJustificatifLink(){
        if (justificatif != null)
            return  ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/rh/api/")
                .path("/absences/")
                .path(String.valueOf(id))
                .path("/justificatif/")
                .path(justificatif)
                .toUriString();

        return null;
    }

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

//    a verifier
    private String type;

    @Column(columnDefinition = "boolean default false")
    private Boolean accepted;

    @ManyToOne
    @JsonIgnoreProperties({"conges","absences","avantages"})
    private Salarie salarie;

    @PrePersist
    void defaultValues() {
        accepted = false;
    }

}
