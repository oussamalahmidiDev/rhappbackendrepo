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
import org.springframework.web.bind.annotation.PostMapping;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
//@EntityListeners(ActivitiesListener.class)
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
//
//@Log4j2
//@Component
//class ActivitiesListener {
//
//    @Autowired
//    private SimpMessagingTemplate template;
//
//    private UserRepository userRepository;
//
//    @Autowired
//    ActivitiesListener(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @PostPersist
//    void send (Activity activity) {
//        log.info("Activity user : {}", activity.getUser().getEmail());
////        template.convertAndSendToUser(activity.getUser().getEmail(), "/topic/activities", activity);
//
//        List<User> agents = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE);
//
//        agents.forEach(agent -> {
//            log.info("Sending activity to : {}", agent.getEmail());
//            template.convertAndSendToUser(agent.getEmail(), "/topic/activities", activity);
//        });
//
//    }
//
//}
