package com.capstone.CapstoneProject.Community.TeamProject;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TagsRepository extends JpaRepository<Tags, Long> {
    List<Tags> findByProjectId(Long projectId);
    List<Tags> findByProjectIdIn(List<Long> projectIds);
}
