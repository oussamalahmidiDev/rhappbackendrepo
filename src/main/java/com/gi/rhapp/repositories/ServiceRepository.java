package com.gi.rhapp.repositories;

import com.gi.rhapp.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository  extends JpaRepository<Service , Long> {
}
