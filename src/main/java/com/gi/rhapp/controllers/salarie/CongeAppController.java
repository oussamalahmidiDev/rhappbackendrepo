package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Conge;
import com.gi.rhapp.models.CongeSalarieRequest;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.TypeConge;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@RestController
@RequestMapping("/salarie/api/conges")
@CrossOrigin("*")
public class CongeAppController {

    Logger log = LoggerFactory.getLogger(CongeAppController.class);

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private  TypeCongeRepository typeCongeRepository;

    @Autowired
    private ProfileAppController profileAppController;

    public Salarie getProfile(){
        return profileAppController.getProfile();
    }


    @GetMapping()
    public List<Conge> getConges () {
        return getProfile().getConges();
    }

    @GetMapping("/{id}")
    public Conge  getOneConge(@PathVariable(value = "id")Long id){
        return congeRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le Conge avec id = " + id + " est introuvable."));
    }

    @PostMapping("/create")
    public Conge createConge (@RequestBody CongeSalarieRequest congeRequest ) {
        Conge conge = congeRequest.getConge();
        TypeConge type = typeCongeRepository.save(TypeConge.builder().typeConge(congeRequest.getTypeConge()).build());
        conge.setType(type);
        System.out.println(conge);

        if (getProfile().getSolde() < conge.getDuree())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Vous ne pouvez pas dépasser %d jours du congé.", getProfile().getSolde()));

        congeRepository.save(Conge.builder()
                .salarie(getProfile())
                .type(type)
                .dateDebut(conge.getDateDebut())
                .dateFin(conge.getDateFin())
                .motif(conge.getMotif())
                .build());
        return conge;
    }

    @PutMapping("/{id}/modifier")
    @Modifying
    public Conge modifyConge(@PathVariable(value = "id")Long id , @RequestBody CongeSalarieRequest congeRequest){
        try{
            System.out.println(congeRequest);
            TypeConge type = typeCongeRepository.save(TypeConge.builder().typeConge(congeRequest.getTypeConge()).build());
            Conge newConge = congeRequest.getConge();
            congeRepository.save(Conge.builder()
                    .salarie(getProfile())
                    .type(type)
                    .id(newConge.getId())
                    .dateCreation(newConge.getDateCreation())
                    .etat(newConge.getEtat())
                    .dateDebut(newConge.getDateDebut())
                    .dateFin(newConge.getDateFin())
                    .motif(newConge.getMotif())
                    .build());

            return newConge;

        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Le congé avec id "+id+" est introuvable");
        }

    }

    @DeleteMapping("/{id}/supprimer")
    public void  deleteConge(@PathVariable(value = "id")Long id){
        congeRepository.deleteById(id);
//        return ResponseEntity.ok("l'Absence est supprimer avec succès");
    }
}
