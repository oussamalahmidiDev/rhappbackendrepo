package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.ActivitiesService;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/rh/api/users")
public class RhUsersController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private ActivitiesService activitiesService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AuthService authService;

    private String service = "Admininstration - Gestion d'utilisateurs";

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getUsers() {
        return repository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE);
    }

    @PostMapping("/create")
    public User createUser(@Valid @RequestBody User user) {
        if (repository.findByEmail(user.getEmail()) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà");

        repository.save(user);
        mailService.sendVerificationMail(user);
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        activitiesService.saveAndSend(
            Activity.builder()
            .evenement("Enregistrement d'un nouveau utilisateur : " + user.getFullname())
            .service(service)
            .user(authService.getCurrentUser())
            .build()
        );
        return user;
    }

    @PutMapping("/{id}/modifier")
    public User updateUser(@PathVariable("id") Long id, @Valid  @RequestBody User user) {
        User userFromDB = getUser(id);
//        if (!repository.findByEmail(user.getEmail()).getId().equals(id))
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà");
        userFromDB.setNom(user.getNom());
        userFromDB.setPrenom(user.getPrenom());
        userFromDB.setEmail(user.getEmail());
        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Modification des informations de " + user.getFullname())
                .service(service)
                .user(authService.getCurrentUser())
                .build()
        );
        return repository.save(userFromDB);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        User user = getUser(id);
        repository.deleteById(id);
        activitiesService.saveAndSend(
            Activity.builder()
                .evenement("Suppression de " + user.getFullname())
                .service(service)
                .user(authService.getCurrentUser())
                .build()
        );
        return new ResponseEntity<>("Utilisateur a été supprimé avec succès", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable(value = "id") Long id) {
        return repository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")
        );
    }

}
