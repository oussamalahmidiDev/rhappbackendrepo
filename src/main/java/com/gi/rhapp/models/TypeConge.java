package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Data
@Builder
public class TypeConge  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Enumerated(EnumType.STRING)
    private String typeConge ;


//    @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
//    private List<Conge> conges;
}
