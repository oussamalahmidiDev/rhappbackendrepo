package com.gi.rhapp.controllers;

import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.MailService;
import com.gi.rhapp.utilities.JwtUtil;
import com.gi.rhapp.utilities.VerificationTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin("*")
public class GenericController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @PostMapping("/api/auth")
    @ResponseBody
    public ResponseEntity<?> authenticate (@RequestBody User credentials)  {
        try {
            authService.authenticate(credentials.getEmail(), credentials.getPassword());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'email ou mot de passe est incorrect");
        }

        final UserDetails userDetails = authService.loadUserByUsername(credentials.getEmail());

        final String token = jwtTokenUtil.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
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
}
