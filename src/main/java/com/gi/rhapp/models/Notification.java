package com.gi.rhapp.models;

import com.gi.rhapp.utilities.VerificationTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@EntityListeners(NotificationListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @CreationTimestamp
    private Date timestamp;

//    @ManyToOne
//    private User from;

    @ManyToMany
    @JsonIgnore
    private List<User> to;

    private Boolean isSeen;

    @PrePersist
    public void intialValues() {
        isSeen = false;
    }

}

// Notification listener to send notification after persisting entity in the DB.
@Component
class NotificationListener {

    @Autowired
    private SimpMessagingTemplate template;

    @PostPersist
    void send (Notification notification) {
        notification.getTo().forEach(receiver -> template.convertAndSendToUser(receiver.getEmail(), "/topic/notifications", notification));
    }

}
