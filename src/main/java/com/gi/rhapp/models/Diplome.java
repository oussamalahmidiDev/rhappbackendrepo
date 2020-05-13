package com.gi.rhapp.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Diplome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Date dateDiplome;
    private Date expDiplome;

    private String path;

    @CreationTimestamp
    private Date dateCreation;
    @UpdateTimestamp
    private Date dateUpdate;

    @ManyToOne
    @JsonIgnoreProperties({"diplomeObt"})
    private Salarie salarie;


}
