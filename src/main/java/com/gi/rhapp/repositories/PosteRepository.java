package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Poste;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PosteRepository extends JpaRepository<Poste , Long> {
}
