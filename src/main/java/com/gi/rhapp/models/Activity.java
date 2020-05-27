package com.gi.rhapp.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gi.rhapp.enumerations.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
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

    @ManyToOne
    @JsonIgnoreProperties({"activities"})
    private User user;

    @CreationTimestamp
    private Date timestamp;

    @Enumerated(EnumType.STRING)
    private Role scope;

    public Activity (String evenement) {
        this.evenement = evenement;
        this.scope = Role.ADMIN;
    }

    public Activity (String evenement, String service) {
        this.evenement = evenement;
        this.service = service;
        this.scope = Role.ADMIN;
    }

    public Activity (String evenement, String service, Role scope) {
        this.evenement = evenement;
        this.service = service;
        this.scope = scope;
    }




}
