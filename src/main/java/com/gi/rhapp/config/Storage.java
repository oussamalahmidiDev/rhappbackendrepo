package com.gi.rhapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class Storage {

    @Value("spring.servlet.multipart.location")
    private static String UPLOAD_DIR = "./src/main/resources/uploads/img";
    private static String DB_PATH = "uploads/img";

    private static String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private static String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";


    public static void saveFile(InputStream inputStream , String path){
        try{
            OutputStream outputStream = new FileOutputStream(new File(path));
            int read = 0;
            byte[] bytes = new byte[1024];
            while((read = inputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,read);
            }
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static ResponseEntity deleteFile(Long id ,String path , String DIR){
        try{
            Path fileToDelete = Paths.get(DIR + File.separator + path).toAbsolutePath().normalize();
            System.out.println(fileToDelete);
            Resource resource = new UrlResource(fileToDelete.toUri());
            resource.getFile().delete();
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Id invalide");
        }

    }

    private String location;

    public String getLocation () {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
