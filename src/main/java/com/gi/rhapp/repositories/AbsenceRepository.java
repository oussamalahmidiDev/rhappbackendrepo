package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsenceRepository  extends JpaRepository<Absence , Long> {
}
