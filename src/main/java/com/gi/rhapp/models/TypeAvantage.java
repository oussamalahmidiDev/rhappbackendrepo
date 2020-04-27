package com.gi.rhapp.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypeAvantage  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeAvanatge;


    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<AvantageNat> avantages;
}
