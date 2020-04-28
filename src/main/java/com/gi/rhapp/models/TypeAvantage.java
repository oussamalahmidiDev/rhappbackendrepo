package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TypeAvantage  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeAvanatge;


    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"type"})
    private List<AvantageNat> avantages;
}
