package com.gi.rhapp.models;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TypeAvantage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeAvanatge;


    @OneToMany(mappedBy = "avantageNat", fetch = FetchType.LAZY)
    private AvantageNat avantageNat;
}
