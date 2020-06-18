package com.gi.rhapp.repositories;

import com.gi.rhapp.models.TypeRetraite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


public interface TypeRetraiteRepository  extends JpaRepository<TypeRetraite , Long> {

    TypeRetraite findFirstByTypeRetraite(String type);
}
