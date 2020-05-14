package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Diplome;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiplomeRepository extends JpaRepository<Diplome,Long> {

//    Diplome findBySalarieId(Long id);
}
