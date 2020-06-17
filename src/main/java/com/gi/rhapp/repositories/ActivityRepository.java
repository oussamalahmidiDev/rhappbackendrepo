package com.gi.rhapp.repositories;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
//    List<Activity> findFirstByOrderByTimestampDesc(int limit);

    Page<Activity> findAllByScope(Role scope, Pageable pageable);

    Page<Activity> findAllByUser(User user, Pageable pageable);
}
