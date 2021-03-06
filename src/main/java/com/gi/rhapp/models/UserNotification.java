package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotification implements Serializable {

    @Id
    @JoinColumn
    @ManyToOne
//    @JsonIgnore
    @JsonUnwrapped
    private Notification notification;

    @Id
    @JoinColumn
    @ManyToOne
    @JsonIgnore
    private User receiver;

    private Boolean isSeen;

    @PrePersist
    public void intialValues() {
        isSeen = false;
    }

}
