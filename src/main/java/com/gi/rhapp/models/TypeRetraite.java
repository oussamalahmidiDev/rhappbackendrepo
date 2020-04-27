package com.gi.rhapp.models;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TypeRetraite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeRetraite;

    @OneToMany(mappedBy = "retraite", fetch = FetchType.LAZY)
    private Retraite retraite;
}
