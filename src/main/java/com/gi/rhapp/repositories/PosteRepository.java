package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Poste;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PosteRepository extends JpaRepository<Poste , Long> {

    List<Poste> findAllByOrderByDateCreationDesc();
}
