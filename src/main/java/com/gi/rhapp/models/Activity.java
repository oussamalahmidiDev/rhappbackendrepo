package com.gi.rhapp.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

@Component
@Log4j2
class ActivitiesListener {

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserRepository userRepository;

    @PostPersist
    void send (Activity activity) {
        template.convertAndSendToUser(activity.getUser().getEmail(), "/topic/activities", activity);

        userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE).forEach(agent -> {
            log.info("Sending activity to : {}", agent.getEmail());
            template.convertAndSendToUser(agent.getEmail(), "/topic/activities", activity);
        });

    }

}
