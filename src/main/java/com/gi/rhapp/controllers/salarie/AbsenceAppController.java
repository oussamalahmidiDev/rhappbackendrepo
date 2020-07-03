package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.config.Storage;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.repositories.AbsenceRepository;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.services.Download;
import com.gi.rhapp.services.Upload;
import com.gi.rhapp.utilities.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/salarie/api/absences")
@CrossOrigin("*")
public class AbsenceAppController {

    Logger log = LoggerFactory.getLogger(AbsenceAppController.class);


    @Autowired
    private ProfileAppController profileAppController;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private Upload upload;

    @Autowired
    private Download download;

    @Autowired
    private ActivityRepository activityRepository;

    String service = "Panneau de salarié - Demandes de Absence";


    private static String UPLOAD_JUSTIF_DIR = "./src/main/resources/uploads/justificatifs";


    public Salarie getProfile(){
        return profileAppController.getProfile();
    }


    @GetMapping()
    public List<Absence> getAbsences () {

        return getProfile().getAbsences();
    }

    @GetMapping("/{id}")
    public Absence getOneAbsence (@PathVariable("id")Long id) {
        activityRepository.save(
                Activity.builder()
                        .evenement("Voir la liste des absences")
                        .service(service)
                        .user(getProfile().getUser())
                        .scope(Role.SALARIE)
                        .build()
        );
        return absenceRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "L'absence avec id = " + id + " est introuvable."));
    }

    @PostMapping("{id}/upload/justification")
    public ResponseEntity uploadJustif(@PathVariable("id") Long id , @RequestParam("file") MultipartFile file ) throws ParseException {

        try{
            Absence absence = absenceRepository.findById(id).get();
            String fileName = upload.uploadJustificatif(file);
            absence.setJustificatif(fileName);
//            absence.setDescription(description);

            absenceRepository.save(absence);

            activityRepository.save(
                    Activity.builder()
                            .evenement("Ajouté la justification de l'absence : " +id)
                            .service(service)
                            .user(getProfile().getUser())
                            .scope(Role.SALARIE)
                            .build()
            );
            return new ResponseEntity(HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'absence avec id = " + id + " est introuvable.");
        }


    }

    @PostMapping("{id}/description")
    public ResponseEntity addDescription(@PathVariable("id") Long id , @RequestParam("description") String description ) throws ParseException {

        try{
            Absence absence = absenceRepository.findById(id).get();
            absence.setDescription(description);
            absenceRepository.save(absence);

            activityRepository.save(
                    Activity.builder()
                            .evenement("Ajouté une description pour l'absence : "+ absence.getId())
                            .service(service)
                            .user(getProfile().getUser())
                            .scope(Role.SALARIE)
                            .build()
            );

            return new ResponseEntity(HttpStatus.OK);
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "L'absence avec id = " + id + " est introuvable.");
        }

    }

//    @PostMapping("{id}/upload/justif")
//    public ResponseEntity uploadJusti(@PathVariable("id") Long id , @RequestParam("file") MultipartFile file) throws ParseException {
//
//        activityRepository.save(
//                Activity.builder()
//                        .evenement("Le salarié " + getProfile().getUser().getFullname() + " a ajouté un diplôme")
//                        .service(service)
//                        .user(getProfile().getUser())
//                        .scope(Role.RH)
//                        .build()
//        );
//        return upload.uploadDiplome(file,name,dateDiplome,expDiplome,getProfile());

//    }

//    @PostMapping(value = "/download/justification")
//    public ResponseEntity<?> getDiplome(HttpServletResponse response , @RequestParam("fileName") String name ) throws IOException {
//        return download.loadImage(response,name,UPLOAD_JUSTIF_DIR);
//    }

    @PostMapping(value = "/download/justification")
    public ResponseEntity<?> getjuftification(HttpServletResponse response , @RequestParam("fileName") String name ) throws IOException {
        activityRepository.save(
                Activity.builder()
                        .evenement("Téléchargé la justification : " + name)
                        .service(service)
                        .user(getProfile().getUser())
                        .scope(Role.SALARIE)
                        .build()
        );
        return download.loadImage(response,name,UPLOAD_JUSTIF_DIR);
    }

    @DeleteMapping("{id}/justification/delete")
    public ResponseEntity deleteDiplome(@PathVariable Long id){
        Absence absence = absenceRepository.findById(id)
                .orElseThrow( ()->new
                        ResponseStatusException(HttpStatus.NOT_FOUND, "L'absence avec id = " + id + " est introuvable."));
            String path = absenceRepository.getOne(id).getJustificatif();
            Storage.deleteFile(id,path,UPLOAD_JUSTIF_DIR);
            absence.setJustificatif(null);
            absenceRepository.save(absence);
            return new ResponseEntity(HttpStatus.OK);
    }
}
