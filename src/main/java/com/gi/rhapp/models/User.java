package com.gi.rhapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.utilities.VerificationTokenGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String nom;
    private String prenom;
    private String photo;
    private String telephone;

    @Transient
    private String avatarLink;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String newPassword;

    private Boolean emailConfirmed;

    private String verificationToken;

    @CreationTimestamp
    private Date dateCreation;

    @UpdateTimestamp
    private Date dateModification;

    @OneToOne(mappedBy = "user" ,cascade={CascadeType.ALL,CascadeType.REMOVE})
    @JsonIgnoreProperties(value = {"user"}, allowSetters = true)
    private Salarie salarie;

    @OneToMany(mappedBy = "user", cascade={CascadeType.ALL,CascadeType.REMOVE})
    @JsonIgnoreProperties({"user"})
    private List<Activity> activities;

    @OneToMany(mappedBy = "from")
    @JsonIgnoreProperties({"from", "to"})
    @JsonIgnore
    private List<Notification> notifications;


    // USER DETAILS IMPLEMENTATION

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_" + role)); // ROLE_  : just a prefix , Spring build a prefix for every role its just a syntx ,for exemple if your role is USER then your authority(role) is ROLE_USER

        return roles;
    }

    @PrePersist
    public void intialValues() {
        verificationToken = VerificationTokenGenerator.generateVerificationToken();
        emailConfirmed = false;
//        email = email.trim().toLowerCase();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    }

    public String getFullname() { return nom + " " + prenom; }

    public void setEmail(String email) {
        this.email = email.trim().toLowerCase();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }


}
