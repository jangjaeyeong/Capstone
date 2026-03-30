package com.capstone.CapstoneProject.Community.TeamProject.Repository;

import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRolesRepository extends JpaRepository<ProjectRoles, Long> {
    List<ProjectRoles> findByProjectId(Long projectId);
    List<ProjectRoles> findByProjectIdIn(List<Long> projectIds);
    long deleteByProjectId(Long projectId);

}
