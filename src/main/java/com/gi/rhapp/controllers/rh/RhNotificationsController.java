package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.UserNotification;
import com.gi.rhapp.repositories.NotificationRepository;
import com.gi.rhapp.repositories.UserRepository;
import com.gi.rhapp.services.AuthService;
import com.gi.rhapp.services.NotificationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rh/api/notifications")
@Log4j2
public class RhNotificationsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @GetMapping()
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
