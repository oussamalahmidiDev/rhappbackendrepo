package com.gi.rhapp.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String evenement;
    private String service;

    @CreationTimestamp
    private Date timestamp;

    public Activity (String evenement) {
        this.evenement = evenement;
    }

    public Activity (String evenement, String service) {
        this.evenement = evenement;
        this.service = service;
    }


}
