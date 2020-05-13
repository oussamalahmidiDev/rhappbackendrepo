package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import net.bytebuddy.asm.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.synchronoss.cloud.nio.multipart.Multipart;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/salarie/api")
@CrossOrigin("*")
public class SalarieAppController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private CongeRepository congeRepository;

    @Autowired
    private TypeCongeRepository typeCongeRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private Upload upload;

    Logger log = LoggerFactory.getLogger(SalarieAppController.class);

    @Value("spring.servlet.multipart.location")
    private static String UPLOAD_DIR = "../rhappsalarierepo/src/assets/img/profile";
    private static String FRONT_DIR = "assets/img/profile";
    private static String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private static String UPLOAD_diplome_DIR = "./src/main/resources/uploads/diplomes";


    @GetMapping("/profil")
    public Salarie getProfile () {
        return salarieRepository.findById(1L).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id invalide")
        );
    }


    @GetMapping("/absences")
    public List<Absence> getAbsences () {
        return getProfile().getAbsences();
    }

//    @PostMapping("/absences/create")
//    public Absence createAbsence(@RequestBody Absence absence) {
//        absence.setSalarie(getProfile());
//        return absenceRepository.save(absence);
//    }

//    @PutMapping("/profil/modifier")
//    public Salarie modifierProfil(@RequestBody Salarie profil) {
//        return salarieRepository.save(profil);
//    }

    @PutMapping("/profil/modifier/user")
    public User modifierProfilUser(@RequestBody User user) {
        user.setSalarie(getProfile());
        return userRepository.save(user);
    }
    @PutMapping("/profil/modifier/contacts")
    public Salarie modifierProfilContact(@RequestBody Salarie salarie) {
        salarie.setUser(getProfile().getUser());
        salarie.setPoste(getProfile().getPoste());
        salarie.setRetraite(getProfile().getRetraite());

        return salarieRepository.save(salarie);
    }

    @GetMapping("/conges")
    public List<Conge> getConges () {
        return getProfile().getConges();
    }

    @GetMapping("/conges/{id}")
    public Conge  getOneConge(@PathVariable(value = "id")Long id){
        return congeRepository.findById(id).orElseThrow( ()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Le Conge avec id = " + id + " est introuvable."));
    }

    @PostMapping("/conges/create")
    public Conge createConge (@RequestBody Conge conge) {
        System.out.println(conge);
        if (getProfile().getSolde() < conge.getDuree())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, String.format("Vous ne pouvez pas dépasser %d jours du congé.", getProfile().getSolde()));

        conge.setSalarie(getProfile());
        return congeRepository.save(conge);
    }

    @PutMapping("/conges/{id}/modifier")
    @Modifying
    public Conge modifyConge(@PathVariable(value = "id")Long id , @RequestBody Conge conge){
        congeRepository.deleteById(id);
        conge.setId(id);
        return congeRepository.save(conge);
    }

    @DeleteMapping("/conges/{id}/supprimer")
    public void  deleteConge(@PathVariable(value = "id")Long id){
        congeRepository.deleteById(id);
//        return ResponseEntity.ok("l'Absence est supprimer avec succès");
    }

    @GetMapping("/retraites")
    public Retraite getRetraites(){ return getProfile().getRetraite();
    }
    @GetMapping("/avantages")
    public Collection<AvantageNat> getAvantages(){
        return getProfile().getAvantages();
    }

//    @GetMapping("/typesConges")
//    public void typesOfConge(){
//        System.out.println(TypeConge.class.getEnumConstants().toString());
//    }

//   to test email service
    @GetMapping("/confirm")
    public ResponseEntity<String> testConfirmation() {
        mailService.sendVerificationMail(getProfile().getUser());
        return ResponseEntity.ok("");
    }

    @PostMapping("/profil/upload/image")
    public String uploadImage(@RequestParam("file") MultipartFile file){
        Date date= new Date();
        String extension = file.getContentType().split("/")[1];
        if(extension.equals("png") || extension.equals("jpeg") ){
        try{

            String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
            String path = UPLOAD_DIR + File.separator +fileName;
//            System.out.println(path);
//            System.out.println(file.getContentType().split("/")[1]);
            String frontPath = FRONT_DIR+File.separator +fileName;
            User user = getProfile().getUser();
            user.setPhoto(frontPath);
            userRepository.save(user);
            upload.saveFile(file.getInputStream(),path);
            return fileName;
        }catch (Exception e){
            e.printStackTrace();
        }}
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");
        }
        return null;

    }

    @PostMapping("/profil/upload/cv")
    public String uploadCv(@RequestParam("file") MultipartFile file){
        String extension = file.getContentType().split("/")[1];
        if(extension.equals("pdf") ){

            try{
            String fileName = file.getOriginalFilename();
            String path = UPLOAD_CV_DIR + File.separator +fileName;
            Salarie salarie = getProfile();
            salarie.setCv(path);
            salarieRepository.save(salarie) ;
            upload.saveFile(file.getInputStream(),path);
            return fileName;
        }catch (Exception e){
            e.printStackTrace();
        }}
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");

        return null;

    }

    @PostMapping("/profil/upload/diplome")
    public ResponseEntity addDiplome(@RequestParam("file") MultipartFile file ,
                              @RequestParam("name") String  name ,
                              @RequestParam("dateDiplome") String dateDiplome ,
                              @RequestParam("expDiplome") String expDiplome  ) throws ParseException {
        Date date = new Date();
        System.out.println(file.getOriginalFilename());
        System.out.println(dateDiplome);

        try{
            String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
            String path = UPLOAD_diplome_DIR + File.separator +fileName;
            if(!expDiplome.equals("")){
            diplomeRepository.save(Diplome.builder()
                    .salarie(getProfile())
                    .name(name)
                    .dateDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(dateDiplome))
                    .expDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(expDiplome))
                    .path(path)
                    .build());}
            else{
                diplomeRepository.save(Diplome.builder()
                        .salarie(getProfile())
                        .name(name)
                        .dateDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(dateDiplome))
                        .path(path)
                        .build());
            }
            upload.saveFile(file.getInputStream(),path);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/profil/upload/diplome/{id}/delete")
    public ResponseEntity deleteDiplome(@PathVariable Long id){
       try{
           diplomeRepository.deleteById(id);
           return new ResponseEntity(HttpStatus.OK);
       }catch (Exception e){
           throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Id invalide");
       }

    }





}
