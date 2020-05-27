package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Conge;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CongeRepository extends JpaRepository<Conge , Long> {
    List<Conge> findAllBySalarie(Salarie profile);
    List<Conge> findAllByOrderByDateCreationDesc();
}
