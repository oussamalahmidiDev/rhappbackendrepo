package com.gi.rhapp.services;

import com.gi.rhapp.config.Storage;
import com.gi.rhapp.models.Diplome;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.DiplomeRepository;
import com.gi.rhapp.repositories.SalarieRepository;
import com.gi.rhapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Service
public class Upload {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private DiplomeRepository diplomeRepository;

    @Value("spring.servlet.multipart.location")
    private static String UPLOAD_IMAGE_DIR = "./src/main/resources/uploads/img";
    private static String DB_PATH = "uploads/img";

    private static String UPLOAD_CV_DIR = "./src/main/resources/uploads/cv";
    private static String UPLOAD_DIPLOME_DIR = "./src/main/resources/uploads/diplomes";

    public ResponseEntity uploadDiplome(MultipartFile file ,
                                     String  name ,
                                      String dateDiplome ,
                                      String expDiplome , Salarie salarie ) throws ParseException {
        Date date = new Date();
        System.out.println(file.getOriginalFilename());
        System.out.println(dateDiplome);

        try{
            String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
            String path = UPLOAD_DIPLOME_DIR + File.separator +fileName;
            if(!expDiplome.equals("")){
                diplomeRepository.save(Diplome.builder()
                        .salarie(salarie)
                        .name(name)
                        .dateDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(dateDiplome))
                        .expDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(expDiplome))
                        .path(fileName)
                        .build());}
            else{
                diplomeRepository.save(Diplome.builder()
                        .salarie(salarie)
                        .name(name)
                        .dateDiplome(new SimpleDateFormat("yyyy-mm-dd").parse(dateDiplome))
                        .path(fileName)
                        .build());
            }
            Storage.saveFile(file.getInputStream(),path);
            return new ResponseEntity(HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

    }

    public ResponseEntity uploadCv(MultipartFile file,Salarie salarie){
        String extension = file.getContentType().split("/")[1];
        Date date= new Date();
        if(extension.equals("pdf") ){

            try{
//                String fileName = Long.toString(date.getTime())+"."+file.getContentType().split("/")[1];
                String fileName = salarie.getUser().getNom()+salarie.getUser().getPrenom()+"_CV"+"."+file.getContentType().split("/")[1];
                String path = UPLOAD_CV_DIR + File.separator +fileName;
                salarie.setCv(fileName);
                salarieRepository.save(salarie) ;
                Storage.saveFile(file.getInputStream(),path);
                return new ResponseEntity(HttpStatus.OK);
            }catch (Exception e){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Failed Upload , try again!");

            }
        }
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Format incorrect");


    }


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
