package com.capstone.CapstoneProject.Community.TeamProject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TeamProjectRepository extends JpaRepository<TeamProject, Long> {
    Optional<TeamProject> findById(int id);
    Page<TeamProject> findAll(Pageable pageable);
    @Query(value = "select * from team_project where  title LIKE CONCAT('%', :keyword, '%')" +
            " order by created_date desc",
            countQuery = "select count(*) from team_project where title LIKE CONCAT" +
                    "('%', :keyword, '%')",
            nativeQuery = true)
    Page<TeamProject> fullTextSearch(@Param("keyword")String title, Pageable pageable);

    Page<TeamProject> findByTitleContaining(@Param("keyword")String title, Pageable pageable);
}
