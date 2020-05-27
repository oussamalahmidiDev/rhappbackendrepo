package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Direction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DirectionRepository extends JpaRepository<Direction , Long> {

}
