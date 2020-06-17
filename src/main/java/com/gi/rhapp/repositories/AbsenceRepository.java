package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AbsenceRepository  extends JpaRepository<Absence , Long> {
//    List<Absence> findAllBySalarieOrOrderByDateCreationDesc(Salarie salarie);

    List<Absence> findAllByOrderByDateCreationDesc();
    Optional<List<Absence>> findBySalarieId(Long id);

    Optional<Absence> findByIdAndSalarie(Long id, Salarie salarie);

    @Query("SELECT max(dateFin) FROM Absence WHERE salarie.id = ?1")
    Date getMaxDate(Long salarieId);

}

