package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Notification;
import com.gi.rhapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByTo(User user);
}
