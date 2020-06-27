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

    @Autowired
    private Download downloadService;


    Logger log = LoggerFactory.getLogger(ProfileAppController.class);


    private String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";
    private String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";


    @GetMapping()
    public Salarie getProfile() {
//        long id =authService.getCurrentUser().getSalarie().getId();
        try {
//            System.out.println("SALARIE : ");
//            System.out.println(authService.getCurrentUser().getSalarie().getId());
//            Salarie salarie = salarieRepository.findById(authService.getCurrentUser().getSalarie().getId()).get();
//            salarieService.addProperties(authService.getCurrentUser().getSalarie());

            return authService.getCurrentUser().getSalarie();

//            return salarie;
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Le salarie  est introuvable." + e.toString());
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
        Boolean isMatch = encoder.matches(user.getPassword(), currentUser.getPassword());
        if (isMatch) {
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
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Votre ancien mot de passe est incorrect");
        }
    }

    @PutMapping("/modifier/contact")
    public Salarie modifierProfilContact(@RequestBody SalarieProfileRequest salarieProfileRequest) {

        Salarie originSalarie = getProfile();
        log.info("MODIFY SALARIE : " + salarieProfileRequest.getLieuNaissance());
        if (salarieProfileRequest.getLieuNaissance() != null) {
            originSalarie.setLieuNaissance(salarieProfileRequest.getLieuNaissance());
        }
        if (salarieProfileRequest.getDateNaissance() != null) {
            originSalarie.setDateNaissance(salarieProfileRequest.getDateNaissance());
        }
        if (salarieProfileRequest.getAdresse() != null) {
            originSalarie.setAdresse(salarieProfileRequest.getAdresse());
        }
        if (salarieProfileRequest.getEtatFamiliale() != null) {
            originSalarie.setEtatFamiliale(salarieProfileRequest.getEtatFamiliale());
        }
        if (salarieProfileRequest.getNmbEnf() != 0) {
            originSalarie.setNmbEnf(salarieProfileRequest.getNmbEnf());
        }
        if (salarieProfileRequest.getCinUrg() != null) {
            originSalarie.setCinUrg(salarieProfileRequest.getCinUrg());
        }
        if (salarieProfileRequest.getAdresseUrg() != null) {
            originSalarie.setAdresseUrg(salarieProfileRequest.getAdresseUrg());
        }
        if (salarieProfileRequest.getEmailUrg() != null) {
            originSalarie.setEmailUrg(salarieProfileRequest.getEmailUrg());
        }
        if (salarieProfileRequest.getNomUrg() != null) {
            originSalarie.setNomUrg(salarieProfileRequest.getNomUrg());
        }
        if (salarieProfileRequest.getPrenomUrg() != null) {
            originSalarie.setPrenomUrg(salarieProfileRequest.getPrenomUrg());
        }
        if (salarieProfileRequest.getTelephoneUrg() != null) {
            originSalarie.setTelephoneUrg(salarieProfileRequest.getTelephoneUrg());
        }
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

    //just in case the first method dsnt work , tired to fix it
//    @PutMapping("/modifier/contacts")
//    public Salarie modifierProfilContactTwo(@RequestParam("lieuNaissance") String lieuNaissance,
//                                            @RequestParam("adresse") String adresse,
//                                            @RequestParam("etatFamiliale") String etatFamiliale,
//                                            @RequestParam("nmbEnf") int nmbEnf,
//                                            @RequestParam("cinUrg") String cinUrg,
//                                            @RequestParam("adresseUrg") String adresseUrg,
//                                            @RequestParam("emailUrg") String emailUrg,
//                                            @RequestParam("nomUrg") String nomUrg,
//                                            @RequestParam("prenomUrg") String prenomUrg,
//                                            @RequestParam("telephoneUrg") String telephoneUrg,
//                                            @RequestBody Date dateNaissance
//
//    ) throws ParseException {
////        System.out.println(salarie.getLieuNaissance());
////        System.out.println(salarie.getSolde());
//        Salarie originSalarie = getProfile();
//        if(lieuNaissance !=null){ originSalarie.setLieuNaissance(lieuNaissance); }
//        if(dateNaissance !=null){ originSalarie.setDateNaissance(dateNaissance); }
//        if(adresse !=null){originSalarie.setAdresse(adresse);}
//        if(etatFamiliale !=null){originSalarie.setEtatFamiliale(etatFamiliale);}
//        if(nmbEnf !=0){originSalarie.setNmbEnf(nmbEnf);}
//        if(cinUrg !=null){originSalarie.setCinUrg(cinUrg);}
//        if(adresseUrg !=null){originSalarie.setAdresseUrg(adresseUrg);}
//        if(emailUrg !=null){originSalarie.setEmailUrg(emailUrg);}
//        if(nomUrg !=null){originSalarie.setNomUrg(nomUrg);}
//        if(prenomUrg !=null){originSalarie.setPrenomUrg(prenomUrg);}
//        if(telephoneUrg !=null){originSalarie.setTelephoneUrg(telephoneUrg);}
//
//        activityRepository.save(
//                Activity.builder()
//                        .evenement("Le salarié " + getProfile().getUser().getFullname() + " a modifié ses informations de contact")
//                        .service(service)
//                        .user(getProfile().getUser())
//                        .scope(Role.RH)
//                        .build()
//        );
//
//
//
//        return salarieRepository.save(originSalarie);
//    }


    @PostMapping("/upload/image")
    public ResponseEntity uploadImage(@RequestParam("file") MultipartFile file) {

        try{
            String fileName = upload.storeImage(file);
            String path = UPLOAD_IMAGE_DIR + File.separator +fileName;
            User user = getProfile().getUser();
            user.setPhoto(fileName);
            userRepository.save(user);
            Storage.saveFile(file.getInputStream(),path);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");
        }
    }

    @PostMapping(value = "/download/image")
    public ResponseEntity<?> getImage(HttpServletResponse response, @RequestParam("pictureName") String name) throws IOException {
        return download.loadImage(response, name, UPLOAD_IMAGE_DIR);

    }

//    @GetMapping("/download/image")
//    public ResponseEntity<Resource> getAvatar(HttpServletRequest request, @RequestParam("filename") String filename) throws IOException {
//        User user = getProfile().getUser();
//
//        if (user.getPhoto() == null)
//            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");
//
//        if (!user.getPhoto().equals(filename))
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
////        Resource resource = downloadService.loadImage(filename,UPLOAD_IMAGE_DIR);
//
//        // setting content-type header
//        String contentType = null;
//        try {
//            // setting content-type header according to file type
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException e) {
//            System.out.println("Type indéfini.");
//        }
//        // setting content-type header to generic octet-stream
//        if (contentType == null) {
//            contentType = "application/octet-stream";
//        }
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }

    @PostMapping(value = "/download/cv")
    public ResponseEntity<?> getCV(HttpServletResponse response, @RequestParam("cvName") String name) throws IOException {
        return download.loadImage(response, name, UPLOAD_CV_DIR);
    }

    @PostMapping(value = "/download/diplome")
    public ResponseEntity<?> getDiplome(HttpServletResponse response, @RequestParam("diplomeName") String name) throws IOException {
        return download.loadImage(response, name, UPLOAD_DIPLOME_DIR);
    }

    @PostMapping("/upload/cv")
    public ResponseEntity uploadCv(@RequestParam("file") MultipartFile file) {

        Salarie salarie = getProfile();
        try{
                String fileName = upload.uploadCv(file);
                String path = UPLOAD_CV_DIR + File.separator +fileName;
                salarie.setCv(fileName);
                salarieRepository.save(salarie) ;
                Storage.saveFile(file.getInputStream(),path);


                activityRepository.save(
                        Activity.builder()
                                .evenement("Le salarié " + getProfile().getUser().getFullname() + " a ajouté son CV")
                                .service(service)
                                .user(getProfile().getUser())
                                .scope(Role.RH)
                                .build()
                );

                return new ResponseEntity(HttpStatus.OK);
            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");

            }

    }

    @PostMapping("/upload/diplome")
    public ResponseEntity uploadDiplome(@RequestParam("file") MultipartFile file,
                                        @RequestParam("name") String name,
                                        @RequestParam("dateDiplome") Date dateDiplome,
                                        @RequestParam("expDiplome") String expDiplome) throws ParseException {

        Salarie salarie = getProfile();
        try {
            String fileName = upload.uploadJustificatif(file);
            String path = UPLOAD_DIPLOME_DIR + File.separator + fileName;
            if (!expDiplome.equals("")) {
                System.out.println(expDiplome);
                diplomeRepository.save(Diplome.builder()
                        .salarie(salarie)
                        .name(name)
                        .expDiplome(new SimpleDateFormat("yyyy-MM-dd").parse(expDiplome))
                        .dateDiplome(dateDiplome)
                        .path(fileName)
                        .build());
            } else {
                diplomeRepository.save(Diplome.builder()
                        .salarie(salarie)
                        .name(name)
                        .dateDiplome(dateDiplome)
                        .path(fileName)
                        .build());
            }

            activityRepository.save(Activity.builder()
                    .evenement("Ajout de diplôme de " + name )
                    .service(service)
                    .user(getProfile().getUser())
                    .scope(Role.RH)
                    .build());

            Storage.saveFile(file.getInputStream(), path);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


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
