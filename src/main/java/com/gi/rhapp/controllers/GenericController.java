package com.gi.rhapp.controllers;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.utilities.JwtUtil;
import com.gi.rhapp.utilities.VerificationTokenGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin("*")
public class GenericController {

    Logger logger = LoggerFactory.getLogger(GenericController.class);

    @Value("${web.app.uri}")
    private String WEB_APP_URI;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private ActivityRepository activityRepository;

    @PostMapping("/api/auth")
    @ResponseBody
    public ResponseEntity<?> authenticate (@RequestBody User credentials) throws Exception {
        System.out.println(credentials.getEmail() +" "+ credentials.getPassword());

        try {
            authService.authenticate(credentials.getEmail(), credentials.getPassword());

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'email ou mot de passe est incorrect");
        }

        final UserDetails userDetails = authService.loadUserByUsername(credentials.getEmail());

        final String token = jwtTokenUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(credentials.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        activityRepository.save(
            Activity.builder()
                .evenement("L'utilisateur " + user.getFullname() + " s'est connecté sur la plateforme")
                .service("Service d'authentification")
                .user(user)
                .scope(user.getRole().equals(Role.SALARIE)? Role.RH : Role.ADMIN)
                .build()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/istokenvalid")
    @ResponseBody
    public boolean isTokenValid(@RequestParam("token")String token){
        try{
            System.out.println("token is valid : ");
            System.out.println(!jwtTokenUtil.isTokenExpired(token));
            return  !jwtTokenUtil.isTokenExpired(token);
        }catch (Exception e){
            return false;
        }

    }

    @PostMapping("/api/forgot_password")
    @ResponseBody
    public ResponseEntity<?> sendPasswordRecoveryMail (@RequestBody User credentials) {
        User user = userRepository.findByEmail(credentials.getEmail());
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email introuvable");

        user.setVerificationToken(VerificationTokenGenerator.generateVerificationToken());
        userRepository.save(user);

        mailService.sendPasswordRecoveryMail(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Un email de récupération du mot de passe vous e été envoyé. Veuillez verifier votre boîte");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/confirm")
    public String verifyEmail (HttpServletRequest request) {

        User user =  getUserFromToken(request.getParameter("token"));
        user.setEmailConfirmed(true);
        userRepository.save(user);

        String action = request.getParameter("action");
        if (action.equals("forgot_password"))
            return "redirect:/set_password?token=" + user.getVerificationToken();

        if (action.equals("confirm")) {
            if (user.getEmailConfirmed() && user.getPassword() != null)
                throw new ResponseStatusException(HttpStatus.OK);

            user.setVerificationToken(VerificationTokenGenerator.generateVerificationToken());
            userRepository.save(user);
            if (user.getPassword() == null)
                return "redirect:/set_password?token=" + user.getVerificationToken();
        } else
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        return WEB_APP_URI != null? "redirect: " + WEB_APP_URI : "redirect: /";
    }

    @GetMapping("/set_password")
    public String setPasswordView(HttpServletRequest request,  Model model) {
        User user =  getUserFromToken(request.getParameter("token"));
        logger.info("user : {}", user.getPrenom());

        model.addAttribute("user", user);
        return "views/password_setting";
    }

    @PostMapping("/set_password")
    @ResponseBody
    public ResponseEntity<?> handlePasswordForm(@RequestBody HashMap<String, String> request) {
        String password = request.get("password");
        String passwordConf = request.get("passwordconf");
        logger.info("request : {}, {}, {}", request.get("token"), request.get("password"), request.get("passwordconf"));
        User user =  getUserFromToken(request.get("token"));

        if (!password.equals(passwordConf))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les mots de passe ne sont pas identiques");

        if (password.length() < 6)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le mot de passe est très court");

        user.setPassword(encoder.encode(password));
        user.setVerificationToken(VerificationTokenGenerator.generateVerificationToken());
        userRepository.save(user);

        HashMap<String, String> response = new HashMap<>();
        response.put("message", "Le mot de passe a été enregistré avec succès !");
        response.put("redirect_uri", WEB_APP_URI);

        return ResponseEntity.ok(response);
    }

    public User getUserFromToken (String token) {
        logger.info("Verif token : {}", token);
        User userToVerify = userRepository.findOneByVerificationToken(token);
        if (userToVerify == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Token est invalide");

        return userToVerify;
    }
}
