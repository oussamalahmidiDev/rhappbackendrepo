package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Retraite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RetraiteRepository extends JpaRepository<Retraite,Long> {
    Optional<Retraite> findBySalarieId(Long id);


}
