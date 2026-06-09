package com.capstone.CapstoneProject.Community.TeamProject.Repository;

import com.capstone.CapstoneProject.Community.TeamProject.Entity.ProjectMember;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import com.capstone.CapstoneProject.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByUserAndProject(Member user, TeamProject teamProject);
    long countByProject_id(Long projectId);
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByProjectIdIn(List<Long> projectIds);
    long deleteByProjectId(Long projectId);
    Optional<ProjectMember> findByProject_IdAndUser_ProfileName(Long projectId, String username);
    List<ProjectMember> findAllByUser(Member user);
    List<ProjectMember> findAllByProject(ProjectMember project);
}
