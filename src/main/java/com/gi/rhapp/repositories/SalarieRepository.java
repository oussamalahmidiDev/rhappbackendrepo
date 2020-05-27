package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalarieRepository extends JpaRepository<Salarie , Long> {

    List<Salarie> findAllByOrderByDateCreationDesc();

    List<Salarie> findAllByUserNomContainingIgnoreCaseOrUserPrenomContainingIgnoreCaseOrUserEmailContainingIgnoreCaseOrNumSommeContainingIgnoreCaseOrServiceNomContainingIgnoreCaseOrDirectionNomContainingIgnoreCase(
        String nom,
        String prenom,
        String email,
        String numSomme,
        String service,
        String direction
    );
//    Salarie findByNomOr
}
