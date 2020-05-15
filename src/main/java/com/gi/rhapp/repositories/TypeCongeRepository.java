package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Conge;
import com.gi.rhapp.models.TypeConge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TypeCongeRepository extends JpaRepository<TypeConge , Long> {

    TypeConge findFirstByTypeConge (String type);
}
