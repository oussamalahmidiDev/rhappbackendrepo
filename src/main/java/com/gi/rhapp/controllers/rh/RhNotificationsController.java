package com.gi.rhapp.controllers.rh;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.repositories.NotificationRepository;
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

import java.util.List;

@RestController
@RequestMapping("/rh/api/notifications")
@Log4j2
public class RhNotificationsController {

//    @Autowired
//    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuthService authService;

    @GetMapping()
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAllByTo(authService.getCurrentUser(), PageRequest.of(0, 30, Sort.by("timestamp").descending()));
    }

    @PostMapping("/mark_seen")
    public void markAllAsSeen() {
        notificationRepository.findAllByTo(authService.getCurrentUser())
            .forEach(notification -> {
                if (!notification.getIsSeen()) {
                    notification.setIsSeen(true);
                    notificationRepository.save(notification);
                }
            });
    }
//
//    // api to subscribe to notifications event stream via SSE
//    @GetMapping(path = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<Notification> receive() {
//        return Flux.create(sink -> notificationService.subscribe(sink::next));
//    }
//
//    @PostMapping()
//    @ResponseBody
//    public String sendNotification(@RequestBody Notification notification) {
//
//        // we set notification reciever to current client.
//        notificationService.publish(notification);
//        return "OK";
//    }


}
