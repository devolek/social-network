package com.skillbox.socialnetwork.main.repository;

import com.skillbox.socialnetwork.main.model.Role;
import com.skillbox.socialnetwork.main.model.enumerated.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(ERole name);
}
