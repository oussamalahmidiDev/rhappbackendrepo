package com.gi.rhapp.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Service  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nom;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateUpdate;

//    @OneToMany(mappedBy = "service")
////    @JsonIgnoreProperties({"service"})
//    @JsonIgnore
//    private List<Salarie> salaries;
//
//    @OneToMany(mappedBy = "direction")
////    @JsonIgnoreProperties({"service"})
//    @JsonIgnore
//    private List<Poste> postes;
}
