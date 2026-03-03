package com.capstone.CapstoneProject.Community.TeamProject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeamProjectRepository extends JpaRepository<TeamProject, Integer> {
    Optional<TeamProject> findById(int id);
    Page<TeamProject> findAll(Pageable pageable);
}
