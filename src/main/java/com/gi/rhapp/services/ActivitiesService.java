package com.gi.rhapp.services;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.User;
import com.gi.rhapp.repositories.ActivityRepository;
import com.gi.rhapp.repositories.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class ActivitiesService {
    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityRepository activityRepository;

    public void saveAndSend(Activity activity) {
        activityRepository.save(activity);
        log.info("Activity user : {}", activity.getUser().getEmail());

        List<User> agents = userRepository.findAllByRoleIsNotOrderByDateCreationDesc(Role.SALARIE);

        agents.forEach(agent -> {
            log.info("Sending activity to : {}", agent.getEmail());
            template.convertAndSendToUser(agent.getEmail(), "/topic/activities", activity);
        });

    }
}
