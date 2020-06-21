package com.gi.rhapp.controllers.salarie;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.UserNotification;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salarie/api/notifications")
@CrossOrigin("*")
@Component
public class SalarieNotificationsController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;


//    public Salarie getProfile() { return authService.getCurrentUser().getSalarie(); }

    @GetMapping("")
    public List<UserNotification> getAllNotifications() {
        return authService.getCurrentUser().getUserNotification().stream()
                .sorted((o1, o2) -> (int) (o2.getNotification().getTimestamp().getTime() - o1.getNotification().getTimestamp().getTime()))
                .collect(Collectors.toList());
    }

    @PostMapping("/mark_seen")
    public void markAllAsSeen() {

        List<UserNotification> notifications = authService.getCurrentUser().getUserNotification()
                .stream().map(userNotification -> {
                    userNotification.setIsSeen(true);
                    return userNotification;
                }).collect(Collectors.toList());
        authService.getCurrentUser().setUserNotification(notifications);
        userRepository.save(authService.getCurrentUser());
    }

}
