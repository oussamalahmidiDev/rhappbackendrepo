package com.gi.rhapp.repositories;

import com.gi.rhapp.models.AvantageNat;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvantageNatRepository extends JpaRepository<AvantageNat , Long> {

    List<AvantageNat> findBySalarie(Salarie salarie);
    Optional<List<AvantageNat>> findBySalarieId(Long id);
}
