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
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class Download {

    private static String UPLOAD_DIR = "./src/main/resources/uploads/img";


    public ResponseEntity loadImage(HttpServletResponse response ,String name ) throws IOException {
        try {
            Path fileLocation = Paths.get(UPLOAD_DIR + File.separator + name).toAbsolutePath().normalize();
            Resource resource = new UrlResource(fileLocation.toUri());
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            StreamUtils.copy(resource.getInputStream(), response.getOutputStream());
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Image introuvable");
        }
    }
}
