package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Absence;
import com.gi.rhapp.models.Activity;
import com.gi.rhapp.models.Salarie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface SalarieRepository extends JpaRepository<Salarie , Long>, JpaSpecificationExecutor<Salarie> {
    List<Salarie> findAllByOrderByDateCreationDesc();
}
