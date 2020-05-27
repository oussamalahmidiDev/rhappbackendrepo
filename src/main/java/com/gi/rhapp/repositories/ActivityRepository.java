package com.gi.rhapp.repositories;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findAllByOrderByTimestampDesc();

    List<Activity> findAllByScopeOrderByTimestampDesc(Role scope);
}
