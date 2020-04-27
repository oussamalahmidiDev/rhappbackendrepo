package com.gi.rhapp.models;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
public class TypeRetraite  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeRetraite;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    private List<Retraite> retraites;
}
