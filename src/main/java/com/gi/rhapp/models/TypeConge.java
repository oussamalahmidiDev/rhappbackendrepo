package com.gi.rhapp.models;

import lombok.*;

import javax.persistence.*;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TypeConge {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeConge;

    @OneToMany(mappedBy = "conge", fetch = FetchType.LAZY)
    private Conge conge;
}
