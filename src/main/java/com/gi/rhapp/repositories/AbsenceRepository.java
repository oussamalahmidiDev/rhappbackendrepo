package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AbsenceRepository  extends JpaRepository<Absence , Long> {

    List<Absence> findAllBySalarie(Salarie salarie);

}

