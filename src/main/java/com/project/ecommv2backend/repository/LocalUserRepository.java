package com.project.ecommv2backend.repository;

import com.project.ecommv2backend.model.LocalUser;
import org.springframework.cglib.core.Local;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocalUserRepository extends ListCrudRepository<LocalUser, Long> {
    Optional<LocalUser> findByUsernameIgnoreCase(String username);

    Optional<LocalUser> findByEmailIgnoreCase(String email);

   Optional<LocalUser> findUserByEmail(String email);

    LocalUser findByUsername(String username);


}
