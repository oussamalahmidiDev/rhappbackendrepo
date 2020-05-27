package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AbsenceRepository  extends JpaRepository<Absence , Long> {
//    List<Absence> findAllBySalarieOrOrderByDateCreationDesc(Salarie salarie);

    List<Absence> findAllByOrderByDateCreationDesc();
    Optional<List<Absence>> findBySalarieId(Long id);

}

