package com.gi.rhapp.services;

import com.gi.rhapp.config.Storage;
import com.gi.rhapp.exceptions.StorageException;
import com.gi.rhapp.models.Diplome;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.DiplomeRepository;
import com.gi.rhapp.repositories.SalarieRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.utilities.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class Upload {

    private Logger logger = LoggerFactory.getLogger(Upload.class);

    // default upload folder
    private final Path fileStorageLocation ;

    // default allowed extensions (images)
    private final String[] ALLOWED_IMAGES_EXTENSIONS = {
        "jpeg",
        "jpg",
        "png",
    };

    // default allowed extensions (images)
    private final String[] ALLOWED_DOCS_EXTENSIONS = {
        "pdf",
        "doc",
        "docx",
    };

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Autowired
    private FilenameUtils filenameUtils;

    @Value("spring.servlet.multipart.location")
    private static String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";
    private static String DB_PATH = "uploads/img";

    private static String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private static String UPLOAD_JUSTIF_DIR = "./src/main/resources/uploads/justificatifs";
    private static String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";

    public Upload(/*Storage fileStorageProperties*/) {

        // i dont know why i can't inject Storage configuration class in the cunstructor.
        // Please consider checking the static properties and methods.

        this.fileStorageLocation = Paths.get("./src/main/resources/uploads/")
            .toAbsolutePath().normalize();
//        this.fileStorageLocation = Paths.get(fileStorageProperties.getLocation())
//            .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new StorageException( "Erreur de configuration de dossier de fichiers.", e);
        }
    }

    public boolean isExtensionAllowed (String extension, String[] ALLOWED_EXTENSIONS) {
        for (String currentExtension: ALLOWED_EXTENSIONS) {
            if (currentExtension.equalsIgnoreCase(extension))
                return true;
        }
        return false;
    }

    public String uploadJustificatif (MultipartFile file) {
        logger.info("Uploaded file : {}, file size = {}, file type {}", FilenameUtils.getFilename(file), file.getSize(), FilenameUtils.getExtension(file));

        // check file extension
        if (!isExtensionAllowed(FilenameUtils.getExtension(file), ALLOWED_DOCS_EXTENSIONS)
            && !isExtensionAllowed(FilenameUtils.getExtension(file), ALLOWED_IMAGES_EXTENSIONS))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce fichier de type insupporté.");

        // normaliser le nom de fichier avec un nom standard (justificatfif + timestamp)
        String fileName = StringUtils.cleanPath(FilenameUtils.getFilename(file) + "_" + System.currentTimeMillis() + "." + FilenameUtils.getExtension(file));
        logger.info("New file name : {}, file size = {}", fileName, file.getSize());


        try {
            //verifier le nom de fichier
            if (fileName.contains(".."))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de fichier est invalide : " + fileName);

            // chemin de dossier de justificatifs
            Path target = Files.createDirectories(
                Paths.get(fileStorageLocation.toString() + "/justificatifs"))
                .resolve(fileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de stocker le fichier " + fileName);
        }
    }

    public String storeImage (MultipartFile file) {
        logger.info("Uploaded file : {}, file size = {}, file type {}", FilenameUtils.getFilename(file), file.getSize(), FilenameUtils.getExtension(file));

        // check file extension
        if (!isExtensionAllowed(FilenameUtils.getExtension(file), ALLOWED_IMAGES_EXTENSIONS))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce fichier de type insupporté.");

        // normaliser le nom de fichier avec un nom standard (justificatfif + timestamp)
        String fileName = StringUtils.cleanPath(System.currentTimeMillis() + "." + FilenameUtils.getExtension(file));

        logger.info("New file name : {}, file size = {}", fileName, file.getSize());


        try {
            //verifier le nom de fichier
            if (fileName.contains(".."))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de fichier est invalide : " + fileName);

            // chemin de dossier de justificatifs
            Path target = Files.createDirectories(
                Paths.get(fileStorageLocation.toString() + "/avatars"))
                .resolve(fileName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de stocker le fichier " + fileName);
        }
    }

    public void deleteImage (String fileName) {
        try {
            Path filePath = Paths.get(this.fileStorageLocation.toString() + "/avatars").resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists())
                resource.getFile().delete();
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + fileName);

        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



//    public ResponseEntity uploadDiplome(MultipartFile file ,
//                                     String  name ,
//                                      Date dateDiplome ,
//                                      String expDiplome , Salarie salarie ) throws ParseException {
//        Date date = new Date();
//        System.out.println(file.getOriginalFilename());
//        System.out.println(dateDiplome);
//
//        try{
//            String fileName = date.getTime()+"."+file.getContentType().split("/")[1];
//            System.out.println(fileName);
//            String path = UPLOAD_DIPLOME_DIR + File.separator +fileName;
//            if(!expDiplome.equals("")){
//                System.out.println(expDiplome);
//                diplomeRepository.save(Diplome.builder()
//                        .salarie(salarie)
//                        .name(name)
//                        .expDiplome(new SimpleDateFormat("yyyy-MM-dd").parse(expDiplome))
//                        .dateDiplome(dateDiplome)
//                        .path(fileName)
//                        .build());}
//            else{
//                System.out.println("here");
//                diplomeRepository.save(Diplome.builder()
//                        .salarie(salarie)
//                        .name(name)
//                        .dateDiplome(dateDiplome)
//                        .path(fileName)
//                        .build());
//            }
//            Storage.saveFile(file.getInputStream(),path);
//            return new ResponseEntity(HttpStatus.OK);
//        }catch (Exception e){
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }
//
//    }

    public String uploadCv(MultipartFile file){
        String extension = filenameUtils.getExtension(file);
        if(extension.equals("pdf") ){
            try{
                String fileName = StringUtils.cleanPath(System.currentTimeMillis() + "_CV." + FilenameUtils.getExtension(file));
                return fileName;
            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");
            }
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");

    }
//    public ResponseEntity uploadJustification(MultipartFile file,Salarie salarie){
//        String extension = file.getContentType().split("/")[1];
//        Date date= new Date();
//        if(extension.equals("pdf") ){
//
//            try{
////                String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
//                String fileName = salarie.getUser().getNom()+salarie.getUser().getPrenom()+"_Justification"+"."+file.getContentType().split("/")[1];
//                String path = UPLOAD_JUSTIF_DIR + File.separator +fileName;
//                salarie.setCv(fileName);
//                salarieRepository.save(salarie) ;
//                Storage.saveFile(file.getInputStream(),path);
//                return new ResponseEntity(HttpStatus.OK);
//            }catch (Exception e){
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");
//
//            }
//        }
//        else
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");
//
//
//    }



    public ResponseEntity uploadImage(MultipartFile file , Salarie profile){
        Date date= new Date();
        String extension = file.getContentType().split("/")[1];
        if(extension.equals("png") || extension.equals("jpeg") ){
            try{
                String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
                String path = UPLOAD_IMAGE_DIR + File.separator +fileName;
                User user = profile.getUser();
                user.setPhoto(fileName);
                userRepository.save(user);
                Storage.saveFile(file.getInputStream(),path);
                return new ResponseEntity(HttpStatus.OK);
            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");
            }}
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");
        }

    }

}
