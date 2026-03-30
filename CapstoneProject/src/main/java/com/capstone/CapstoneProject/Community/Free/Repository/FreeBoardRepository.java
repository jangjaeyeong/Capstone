package com.capstone.CapstoneProject.Community.Free.Repository;

import com.capstone.CapstoneProject.Community.Free.Entity.FreeBoard;
import com.capstone.CapstoneProject.Community.TeamProject.Entity.TeamProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeBoardRepository extends JpaRepository<FreeBoard, Long> {
    Optional<FreeBoard> findById(Long id);
    Page<FreeBoard> findAll(Pageable pageable);
    @Query(value = "select * from free_board where  title LIKE CONCAT('%', :keyword, '%')" +
            " order by created_date desc",
            countQuery = "select count(*) from free_board where title LIKE CONCAT" +
                    "('%', :keyword, '%')",
            nativeQuery = true)
    Page<FreeBoard> fullTextSearch(@Param("keyword")String title, Pageable pageable);

    Page<FreeBoard> findByTitleContaining(@Param("keyword")String title, Pageable pageable);

}
