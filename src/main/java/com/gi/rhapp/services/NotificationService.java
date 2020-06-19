package com.gi.rhapp.services;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.User;
import com.gi.rhapp.models.UserNotification;
import com.gi.rhapp.repositories.NotificationRepository;
import com.gi.rhapp.repositories.UserRepository;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
//@EnableScheduling
@Log4j2
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    public void send(Notification notification, User ...receivers) {

        List<UserNotification> userNotifications = new ArrayList<>();

        Arrays.stream(receivers).forEach(receiver -> {
            log.info("Adding receiver : {}", receiver.getEmail());
            UserNotification userNotification = UserNotification.builder()
                .notification(notification)
                .receiver(receiver)
                .build();
            userNotifications.add(userNotification);
        });

        log.info("Attaching receiver to the notif");
        notification.setUserNotification(userNotifications);

        log.info("Saving notif");
        repository.save(notification);
        log.info("Notif saved");
    }

}
