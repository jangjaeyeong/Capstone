package com.capstone.CapstoneProject.AICalling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    @Query("select r.roleName from Roles r")
    List<String> findAllRoles();
    Optional<Roles> findByRoleName(String RoleName);
}
