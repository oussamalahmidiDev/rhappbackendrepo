package com.gi.rhapp.services;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.utilities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

//        System.out.println(user.getEmail());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.getAuthorities());
    }

    public User authenticate(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User authenticatedUser = userRepository.findByEmail(email);
            if (authenticatedUser.getRole().equals(Role.SALARIE))
                if (authenticatedUser.getSalarie().getDeleted())
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est supprimé");
            return authenticatedUser;
        } catch (DisabledException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Votre compte est desactivé");
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "L'email ou mot de passe est incorrect.");
        }
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
        if (principal instanceof UserDetails) {
            return userRepository.findByEmail(((UserDetails) principal).getUsername());

        } else {
            System.out.println("TOSTRING : " + principal.toString());
            return null;
        }
    }
//
//    public User getCurrentUser() {
//        try {
//            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            System.out.println(((UserDetails) principal).getUsername());
//            return userRepository.findByEmail(((UserDetails) principal).getUsername());
//        }catch (Exception e){
//            return null;
//        }
//
//
//    }

}
