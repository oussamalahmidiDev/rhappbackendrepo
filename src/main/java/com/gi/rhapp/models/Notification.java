package com.gi.rhapp.models;

import com.gi.rhapp.utilities.VerificationTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @CreationTimestamp
    private Date timestamp;

    @ManyToOne
    private User from;

    @ManyToMany
    @JsonIgnore
    private List<User> to;

    private Boolean isSeen;

    @PrePersist
    public void intialValues() {
        isSeen = false;
    }

}
