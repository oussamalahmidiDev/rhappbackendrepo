package com.gi.rhapp.models;

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
@Data
@Builder
public class Direction  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateUpdate;

    @OneToMany(mappedBy = "direction")
    @JsonIgnoreProperties({"direction"})
    private List<Salarie> salarie;


    @OneToMany(mappedBy = "direction")
    @JsonIgnoreProperties({"direction"})
    private List<Poste> postes;

}
