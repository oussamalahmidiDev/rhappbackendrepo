package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.config.Storage;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.Download;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



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
    private ResourceLoader resourceLoader;



    Logger log = LoggerFactory.getLogger(ProfileAppController.class);


    private  String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private  String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";
    private  String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";



    @GetMapping()
    public Salarie getProfile() {
        return salarieRepository.findById(1L).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id invalide")
        );
    }

    @PutMapping("/modifier/user")
    public User modifierProfilUser(@RequestBody User user) {
        user.setSalarie(getProfile());
        return userRepository.save(user);
    }

    @PutMapping("/modifier/user/password")
    public ResponseEntity modifierPasswordUser(@RequestBody User user) {
        User currentUser = getProfile().getUser();
        if(currentUser.getPassword().equals(user.getPassword())){
            user.setSalarie(getProfile());
            user.setPassword(user.getNewPassword());
            userRepository.save(user);
            return new ResponseEntity(HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "Votre ancien mot de passe est incorrect");
        }
    }
    @PutMapping("/modifier/contacts")
    public Salarie modifierProfilContact(@RequestBody Salarie salarie) {
        salarie.setUser(getProfile().getUser());
        salarie.setPoste(getProfile().getPoste());
        salarie.setRetraite(getProfile().getRetraite());

        return salarieRepository.save(salarie);
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
        return upload.uploadCv(file,getProfile());
    }

    @PostMapping("/upload/diplome")
    public ResponseEntity uploadDiplome(@RequestParam("file") MultipartFile file ,
                                     @RequestParam("name") String  name ,
                                     @RequestParam("dateDiplome") String dateDiplome ,
                                     @RequestParam("expDiplome") String expDiplome  ) throws ParseException {
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
