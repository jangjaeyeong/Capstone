package com.capstone.CapstoneProject.Community.TeamProject;

import com.capstone.CapstoneProject.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    boolean existsByUserAndProject(Member user, TeamProject teamProject);
    long countByProject_id(Long projectId);
    List<ProjectMember> findByProjectId(Long projectId);
    List<ProjectMember> findByProjectIdIn(List<Long> projectIds);
    long deleteByProjectId(Long projectId);
}
