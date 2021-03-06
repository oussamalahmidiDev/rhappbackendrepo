package com.gi.rhapp.controllers.rh;


import com.gi.rhapp.controllers.salarie.SalarieAppController;
import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.*;
import com.gi.rhapp.repositories.*;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.Download;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.services.Upload;
import com.gi.rhapp.utilities.JwtUtil;
import org.aspectj.weaver.loadtime.Agent;
import org.hibernate.annotations.DynamicUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.io.IOException;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/rh/api/profile")
//@CrossOrigin(origins = "*", methods = RequestMethod.OPTIONS, )
public class RhProfileController {

    @Autowired
    private SalarieRepository salarieRepository;

    @Autowired
    private AbsenceRepository absenceRepository;

    @Autowired
    private RetraiteRepository retraiteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AvantageNatRepository avantageNatRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private Upload uploadService;

    @Autowired
    private Download downloadService;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtTokenUtil;



//    *********************************************** API get rh profile *********************************************************************

    @GetMapping("") //works
//    @PreAuthorize("hasRole('ROLE_RH')")
    public User  getProfile(){
          return authService.getCurrentUser();
    }

    @PostMapping("/modifier")
    public RhProfileModificationResponse modifierProfile(@RequestBody User user) {
        User currentUser = getProfile();
        currentUser.setNom(user.getNom());
        currentUser.setPrenom(user.getPrenom());
        currentUser.setTelephone(user.getTelephone());

        String token = null;

        if (user.getEmail() != null && !user.getEmail().equals(currentUser.getEmail())) {
            currentUser.setEmail(user.getEmail());
            userRepository.save(currentUser);
            token = jwtTokenUtil.generateToken(currentUser);
        }
        userRepository.save(currentUser);

        return RhProfileModificationResponse
            .builder()
            .token(token)
            .user(currentUser).build();
    }

    @PostMapping("/change_password")
    public ResponseEntity<String> changerPassword(@RequestBody PasswordChangeRequest request) {
        User currentUser = getProfile();
        if (!passwordEncoder.matches(request.getOldPassword(), currentUser.getPassword()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'ancien mot de passe est incorrect");

        if (!request.getNewPassword().equals(request.getNewPasswordConf()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La confirmation de mot de passe est erronée");

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/avatar/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("image") MultipartFile image) {
        User user = getProfile();
        String fileName = uploadService.storeImage(image);

        user.setPhoto(fileName);
        userRepository.save(user);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/avatar/{filename}")
    public ResponseEntity<Resource> getAvatar(HttpServletRequest request, @PathVariable("filename") String filename) {
        User user = getProfile();

        if (user.getPhoto() == null)
            throw new ResponseStatusException(HttpStatus.OK, "Pas de photo définie.");

        if (!user.getPhoto().equals(filename))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Resource resource = downloadService.downloadImage(user.getPhoto());

        // setting content-type header
        String contentType = null;
        try {
            // setting content-type header according to file type
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Type indéfini.");
        }
        // setting content-type header to generic octet-stream
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
    }

    @DeleteMapping("/avatar/delete")
    public ResponseEntity<String> deleteAvatar() {
        User user = getProfile();

        // check if client has already uploaded an image
        if (user.getPhoto() != null)
            uploadService.deleteImage(user.getPhoto());
        else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        user.setPhoto(null);
        userRepository.save(user);

        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

//    @GetMapping("me")
    public UserDetails me (Authentication auth) {
        return (UserDetails) auth.getPrincipal();
    }

}

