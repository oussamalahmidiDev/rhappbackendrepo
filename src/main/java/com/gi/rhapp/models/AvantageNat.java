package com.gi.rhapp.models;

import com.gi.rhapp.enumerations.EtatRetraite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class AvantageNat  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String commission, specification;

    private boolean retire;

    @PrePersist
    public void intialValues() {
        retire = false;
    }

    @ManyToOne
    private TypeAvantage type;

    @ManyToOne
    private Salarie salarie;

}
