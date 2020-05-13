package com.gi.rhapp.controllers.admin;

import com.gi.rhapp.models.*;
import com.gi.rhapp.enumerations.*;
import com.gi.rhapp.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
@CrossOrigin(value = "*")

public class AdminAppController {

    @Autowired
    private PosteRepository posteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private ActivityRepository activityRepository;

    Logger log = LoggerFactory.getLogger(AdminAppController.class);

    @GetMapping("/users")
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/activities")
    public List<Activity> getActivities() {
        return activityRepository.findAllByOrderByTimestampDesc();
    }

    @PostMapping("/users/create")
    public User createUser(@RequestBody User user) {
        if (userRepository.findByEmail(user.getEmail()) != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà");

        activityRepository.save(new Activity("Enregistrement d'un nouveau utilisateur de nom " + user.getPrenom(), "Admin - Gestion d'utilisateurs"));
        return userRepository.save(user);
    }

    @PutMapping("/users/{id}/modifier")
    public User updateUser(@PathVariable("id") Long id, @RequestBody User user) {
        User userFromDB = getUser(id);
        user.setId(id);
        if (!userRepository.findByEmail(user.getEmail()).getId().equals(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cet email existe déjà");
        userFromDB.setNom(user.getNom());
        userFromDB.setPrenom(user.getPrenom());
        userFromDB.setEmail(user.getEmail());
        activityRepository.save(new Activity("Modification des informations de " + user.getPrenom(), "Admin - Gestion d'utilisateurs"));
        return userRepository.save(userFromDB);
    }

    @DeleteMapping("/users/{id}/delete")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        activityRepository.save(new Activity("Suppression d'un utilisateur de id = " + id, "Admin - Gestion d'utilisateurs"));
        return new ResponseEntity<>("Utilisateur a été supprimé avec succès", HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable(value = "id") Long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable.")
        );
    }


    @GetMapping("/postes")
    public List<Poste> getPostes() {
        return posteRepository.findAll();
    }

    @GetMapping("/postes/{id}")

    public Poste getPoste(@PathVariable(value = "id") Long id) {
        return posteRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Poste introuvable.")
        );

    }

    @PutMapping(value = "/postes/{id}/modifier")
    public Poste modifyPoste(@PathVariable(value = "id") Long id, @RequestBody Poste poste) {
        Poste posteFromDB = getPoste(id);
        poste.setId(id);
        return posteRepository.save(poste);
    }


    @DeleteMapping(value = "/postes/{id}/supprimer")
    public ResponseEntity<String> deleteClient(@PathVariable(value = "id") Long id) {
        posteRepository.deleteById(id);

        return ResponseEntity.ok("");
    }


    @GetMapping("/salaries")
    public List<Salarie> getSalaries() {
        return salarieRepository.findAll();
    }

    @GetMapping(value = "/salaries/{id}")
    public Salarie getSalarie(@PathVariable(value = "id") Long id) {
        return salarieRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salarié introuvable.")
        );
    }

    @DeleteMapping(value = "/salaries/{id}/supprimer")
    public ResponseEntity<String> deleteSalarie(@PathVariable(value = "id") Long id) {
        salarieRepository.deleteById(id);

        return ResponseEntity.ok("");
    }

    @GetMapping("/conges")
    public List<Conge> getConges() {
        return congeRepository.findAll();
    }

    @GetMapping(value = "/conges/{id}")
    public Conge getConge(@PathVariable(value = "id") Long id) {
        return congeRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Congé introuvable.")
        );
    }

    @DeleteMapping(value = "/conges/{id}/supprimer")
    public ResponseEntity<String> deleteConge(@PathVariable(value = "id") Long id) {
        congeRepository.deleteById(id);
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/conges/{id}/{reponse}")
    public Conge reponseconge(@PathVariable(value = "id") Long id, @PathVariable("reponse") String reponse) {
        Conge conge = getConge(id);

        if (reponse.equals("accepter"))
            conge.setEtat(EtatConge.ACCEPTED);
        if (reponse.equals("refuser"))
            conge.setEtat(EtatConge.REJECTED);

        return congeRepository.save(conge);

    }


}
