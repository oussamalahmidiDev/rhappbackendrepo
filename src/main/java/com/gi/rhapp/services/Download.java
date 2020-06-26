package com.gi.rhapp.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class Download {

    // default upload folder
    private final Path fileStorageLocation;

    public Download (/*Storage fileStorageProperties*/) {

        // i dont know why i can't inject Storage configuration class in the cunstructor.
        // Please consider checking the static properties and methods.
        this.fileStorageLocation = Paths.get("./src/main/resources/uploads/")
            .toAbsolutePath().normalize();
    }

    private  String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";
    private  String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private  String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";

    public Resource downloadJustificatif (String filename) {
        try {
            // recuperer le path de fichier demandé
            Path filePath = Paths.get(this.fileStorageLocation.toString() + "/justificatifs").resolve(filename).normalize();
            System.out.println(filePath);
//            Path filePath = this.fileStorageLocation.resolve(fil).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists())
                return resource;
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        }
    }

    public Resource downloadImage (String filename) {
        try {
            // recuperer le path de fichier demandé
            Path filePath = Paths.get(this.fileStorageLocation.toString() + "/avatars").resolve(filename).normalize();
//            Path filePath = this.fileStorageLocation.resolve(fil).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists())
                return resource;
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        }
    }
    public ResponseEntity loadImage(HttpServletResponse response ,String name ,String DIR) throws IOException {
        try {
            Path fileLocation = Paths.get(DIR + File.separator + name).toAbsolutePath().normalize();
            Resource resource = new UrlResource(fileLocation.toUri());
            System.out.println(resource);
            if(DIR.equals(UPLOAD_IMAGE_DIR)){response.setContentType(MediaType.IMAGE_JPEG_VALUE);}
            else {response.setContentType(MediaType.APPLICATION_PDF_VALUE);}
            StreamUtils.copy(resource.getInputStream(), response.getOutputStream());
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Image introuvable");
        }
    }

    public Resource downloadCV(String filename) {
        try {
            Path filePath = Paths.get(this.fileStorageLocation.toString() + "/cv").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists())
                return resource;
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        }
    }

    public Resource downloadDiplome(String filename) {
        try {
            Path filePath = Paths.get(this.fileStorageLocation.toString() + "/diplomes").resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists())
                return resource;
            else
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fichier introuvable : " + filename);
        }
    }
}
