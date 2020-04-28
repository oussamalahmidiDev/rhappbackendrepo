package com.gi.rhapp.repositories;

import com.gi.rhapp.enumerations.Role;
import com.gi.rhapp.models.Salarie;
import com.gi.rhapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User , Long> {

    List<User> findAllByRole(Role role);

    User findByIdAndRole(Long id , Role role);

    User findBySalarie(Salarie salarie);
}
