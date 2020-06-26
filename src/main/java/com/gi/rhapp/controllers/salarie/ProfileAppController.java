package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.config.Storage;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.*;
import com.gi.rhapp.utilities.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/salarie/api/profil")
@CrossOrigin("*")
@Component
public class ProfileAppController {

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

    @Autowired
    private Download download;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    String service = "Panneau de salarié - Gestion de profile";

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private SalarieService salarieService;


    Logger log = LoggerFactory.getLogger(ProfileAppController.class);


    private  String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private  String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";
    private  String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";



    @GetMapping()
    public Salarie getProfile() {
//        long id =authService.getCurrentUser().getSalarie().getId();
        try{
//            System.out.println("SALARIE : ");
//            System.out.println(authService.getCurrentUser().getSalarie().getId());
//            Salarie salarie = salarieRepository.findById(authService.getCurrentUser().getSalarie().getId()).get();
//            salarieService.addProperties(authService.getCurrentUser().getSalarie());

            return authService.getCurrentUser().getSalarie();

//            return salarie;
        }catch (NoSuchElementException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie  est introuvable." +e.toString());
        }

    }


    @PutMapping("/modifier/user")
    public User modifierProfilUser(@RequestBody User user) {
        User originUser = getProfile().getUser();
        originUser.setEmail(user.getEmail());
        originUser.setNom(user.getNom());
        originUser.setPrenom(user.getPrenom());
        originUser.setTelephone(user.getTelephone());

        activityRepository.save(
            Activity.builder()
                .evenement("Modification des informations de compte")
                .service(service)
                .user(getProfile().getUser())
                .scope(Role.RH)
                .build()
        );
        return userRepository.save(originUser);
    }

    @PutMapping("/modifier/user/password")
    public ResponseEntity modifierPasswordUser(@RequestBody User user) {
        System.out.println(user);
//        user.setPassword(encoder.encode("khalil"));
//        System.out.println(user.getPassword());
        User currentUser = getProfile().getUser();
        Boolean isMatch = encoder.matches(user.getPassword(),currentUser.getPassword());
        if(isMatch){
            user.setSalarie(getProfile());
            user.setPassword(encoder.encode(user.getNewPassword()));
            userRepository.save(user);
            activityRepository.save(
                Activity.builder()
                    .evenement("Modification de mot de passe")
                    .service(service)
                    .user(getProfile().getUser())
                    .scope(Role.RH)
                    .build()
            );
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Votre ancien mot de passe est incorrect");
        }
    }
    @PutMapping("/modifier/contact")
    public Salarie modifierProfilContact(@RequestBody Salarie salarie) {
//        System.out.println(salarie.getLieuNaissance());
//        System.out.println(salarie.getSolde());
        System.out.println();
        Salarie originSalarie = getProfile();
        originSalarie.setDateNaissance(salarie.getDateNaissance());
        originSalarie.setLieuNaissance(salarie.getLieuNaissance());
        originSalarie.setAdresse(salarie.getAdresse());
        originSalarie.setEtatFamiliale(salarie.getEtatFamiliale());
        originSalarie.setNmbEnf(salarie.getNmbEnf());
        originSalarie.setCinUrg(salarie.getCinUrg());
        originSalarie.setAdresseUrg(salarie.getAdresseUrg());
        originSalarie.setEmailUrg(salarie.getEmailUrg());
        originSalarie.setNomUrg(salarie.getNomUrg());
        originSalarie.setPrenomUrg(salarie.getPrenomUrg());
        originSalarie.setTelephoneUrg(salarie.getTelephoneUrg());

        activityRepository.save(
            Activity.builder()
                .evenement("Modification des informations de contact")
                .service(service)
                .user(getProfile().getUser())
                .scope(Role.RH)
                .build()
        );



        return salarieRepository.save(originSalarie);
    }

    //just in case the first method dsnt work , tired to fix it
    @PutMapping("/modifier/contacts")
    public Salarie modifierProfilContactTwo(@RequestParam("lieuNaissance") String lieuNaissance,
                                            @RequestParam("adresse") String adresse,
                                            @RequestParam("etatFamiliale") String etatFamiliale,
                                            @RequestParam("nmbEnf") int nmbEnf,
                                            @RequestParam("cinUrg") String cinUrg,
                                            @RequestParam("adresseUrg") String adresseUrg,
                                            @RequestParam("emailUrg") String emailUrg,
                                            @RequestParam("nomUrg") String nomUrg,
                                            @RequestParam("prenomUrg") String prenomUrg,
                                            @RequestParam("telephoneUrg") String telephoneUrg

    ) {
//        System.out.println(salarie.getLieuNaissance());
//        System.out.println(salarie.getSolde());
        Salarie originSalarie = getProfile();
//        originSalarie.setDateNaissance(dateNaissance);
        if(lieuNaissance !=null){ originSalarie.setLieuNaissance(lieuNaissance); }
        if(adresse !=null){originSalarie.setAdresse(adresse);}
        if(etatFamiliale !=null){originSalarie.setEtatFamiliale(etatFamiliale);}
        if(nmbEnf !=0){originSalarie.setNmbEnf(nmbEnf);}
        if(cinUrg !=null){originSalarie.setCinUrg(cinUrg);}
        if(adresseUrg !=null){originSalarie.setAdresseUrg(adresseUrg);}
        if(emailUrg !=null){originSalarie.setEmailUrg(emailUrg);}
        if(nomUrg !=null){originSalarie.setNomUrg(nomUrg);}
        if(prenomUrg !=null){originSalarie.setPrenomUrg(prenomUrg);}
        if(telephoneUrg !=null){originSalarie.setTelephoneUrg(telephoneUrg);}

        activityRepository.save(
                Activity.builder()
                        .evenement("Le salarié " + getProfile().getUser().getFullname() + " a modifié ses informations de contact")
                        .service(service)
                        .user(getProfile().getUser())
                        .scope(Role.RH)
                        .build()
        );



        return salarieRepository.save(originSalarie);
    }




    @PostMapping("/upload/image")
    public ResponseEntity uploadImage(@RequestParam("file") MultipartFile file) {
            return upload.uploadImage(file, getProfile());
    }

    @PostMapping(value = "/download/image")
    public ResponseEntity<?> getImage(HttpServletResponse response , @RequestParam("pictureName") String name ) throws IOException {
        return download.loadImage(response,name,UPLOAD_IMAGE_DIR);
    }

    @PostMapping(value = "/download/cv")
    public ResponseEntity<?> getCV(HttpServletResponse response , @RequestParam("cvName") String name ) throws IOException {
        return download.loadImage(response,name,UPLOAD_CV_DIR);
    }
    @PostMapping(value = "/download/diplome")
    public ResponseEntity<?> getDiplome(HttpServletResponse response , @RequestParam("diplomeName") String name ) throws IOException {
        return download.loadImage(response,name,UPLOAD_DIPLOME_DIR);
    }

    @PostMapping("/upload/cv")
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file){
        activityRepository.save(
            Activity.builder()
                .evenement("Le salarié " + getProfile().getUser().getFullname() + " a ajouté son CV")
                .service(service)
                .user(getProfile().getUser())
                .scope(Role.RH)
                .build()
        );
        return upload.uploadCv(file,getProfile());
    }

    @PostMapping("/upload/diplome")
    public ResponseEntity uploadDiplome(@RequestParam("file") MultipartFile file ,
                                     @RequestParam("name") String  name ,
                                     @RequestParam("dateDiplome") Date dateDiplome ,
                                     @RequestParam("expDiplome") String expDiplome  ) throws ParseException {

        activityRepository.save(
            Activity.builder()
                .evenement("Ajout de diplôme de " + name)
                .service(service)
                .user(getProfile().getUser())
                .scope(Role.RH)
                .build()
        );
        return upload.uploadDiplome(file,name,dateDiplome,expDiplome,getProfile());

    }



    @DeleteMapping("/upload/diplome/{id}/delete")
    public ResponseEntity deleteDiplome(@PathVariable Long id){
        try{
            String path = diplomeRepository.getOne(id).getPath();
            Storage.deleteFile(id,path,UPLOAD_DIPLOME_DIR);
            diplomeRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Id invalide");
        }

    }

    @DeleteMapping("/upload/cv/delete")
    public ResponseEntity deleteCv(){
        try{
            String path = getProfile().getCv();
            Long id = getProfile().getId();
            Storage.deleteFile(id,path,UPLOAD_CV_DIR);
            Salarie salarie = getProfile();
            salarie.setCv(null);
            salarieRepository.save(salarie);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Cv invalide");
        }

    }

    @DeleteMapping("/upload/image/delete")
    public ResponseEntity deleteImage(){
        try{
            String path = getProfile().getUser().getPhoto();
            Long id = getProfile().getId();
            Storage.deleteFile(id,path,UPLOAD_IMAGE_DIR);
            User user = getProfile().getUser();
            user.setPhoto(null);
            userRepository.save(user);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Image invalide");
        }

    }




}
