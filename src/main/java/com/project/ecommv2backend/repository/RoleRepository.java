package com.project.ecommv2backend.repository;

import com.project.ecommv2backend.model.ERole;
import com.project.ecommv2backend.model.Role;
import org.springframework.data.repository.CrudRepository;


import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Optional<Role> findRoleByName(ERole name);
}
