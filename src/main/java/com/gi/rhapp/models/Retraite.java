package com.gi.rhapp.models;

import com.gi.rhapp.enumerations.EtatRetraite;
import com.gi.rhapp.utilities.VerificationTokenGenerator;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@DynamicUpdate
public class Retraite  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public LocalDate dateRetraite, dateValidation;

    private String remarques;

    @Column(unique = true, nullable = false)
    private String reference;

    @Enumerated(EnumType.STRING)
    private EtatRetraite etat;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

    @ManyToOne
    private TypeRetraite type;

    @OneToOne
    private Salarie salarie;

    @PrePersist
    public void intialValues() {
        etat = EtatRetraite.SCHEDULED;
    }

}
