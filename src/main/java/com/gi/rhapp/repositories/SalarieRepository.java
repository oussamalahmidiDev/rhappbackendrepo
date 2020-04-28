package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalarieRepository extends JpaRepository<Salarie , Long> {

//    Salarie findByNomOr
}
