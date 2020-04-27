package com.gi.rhapp.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class AvantageNat {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commission, specification;

    @ManyToOne
    private TypeAvantage type;

    @ManyToOne
    private Salarie salarie;

}
