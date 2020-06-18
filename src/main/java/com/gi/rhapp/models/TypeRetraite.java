package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class TypeRetraite  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeRetraite;

    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Retraite> retraites;

    public TypeRetraite(String typeRetraite) {
        this.typeRetraite = typeRetraite;
    }
}
