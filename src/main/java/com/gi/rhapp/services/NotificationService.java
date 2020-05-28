package com.gi.rhapp.services;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

//@Service
//@EnableScheduling
@Log4j2
public class NotificationService {


    @Autowired
    private AuthService authService;

    private User currentUser;

    private List<Consumer<Notification>> listeners = new CopyOnWriteArrayList<>();


    // function to subscribe clients to server events (notifications)
    public void subscribe (Consumer<Notification> listener) {
        listeners.add(listener);
        this.currentUser = authService.getCurrentUser();
        log.info("Added a listener, for a total of {} listener{} of ID {}", listeners.size(), listeners.size() > 1 ? "s" : "", currentUser.getId());
    }

    // function to push notification

    public User currentUser() {
        return authService.getCurrentUser();
    }
    public void publish (Notification notification) {
//        log.info("PROCESS NOTIF : {}", notification);
//        if (notification.getFrom().equals(authService.getCurrentUser()))
//            return;

        notification.getTo().stream()
            .forEach(user -> {
                log.info("RECEIVER ID {},  CURRENT USER : {}, FROM : {}",currentUser().getId(), currentUser.getId(), notification.getFrom().getId());
                if (!user.getId().equals(notification.getFrom().getId())) {
                    log.info("RECEIVER ID {} WILL RECEIVE",user.getId());
                    listeners.forEach(c -> {
                        if (user.getId().equals(currentUser().getId()))
                            c.accept(notification);
                    });

                }
            });

//        if (!notification.getTo().contains(authService.getCurrentUser()))
//            listeners.forEach(c -> c.accept(notification));

//        notification.getTo()
//            .forEach(user -> {
//                if (authService.getCurrentUser() == )
//            });
        // make sure the notification is sent only to the according client.
//        if (notification.getClient().getId() == authService.getCurrentUser().getClient().getId())
//            listeners.forEach(c -> c.accept(notification));

    }


}
