package com.gi.rhapp.controllers;

import com.gi.rhapp.models.User;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin("*")
public class GenericController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @PostMapping("/api/auth")
    @ResponseBody
    public ResponseEntity<?> authenticate (@RequestBody User credentials) throws Exception {
        authService.authenticate(credentials.getEmail(), credentials.getPassword());

        final UserDetails userDetails = authService.loadUserByUsername(credentials.getEmail());

        final String token = jwtTokenUtil.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }
}
