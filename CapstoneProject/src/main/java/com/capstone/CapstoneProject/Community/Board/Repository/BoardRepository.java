package com.capstone.CapstoneProject.Community.Board.Repository;

import com.capstone.CapstoneProject.Community.Board.Entity.Board;
import com.capstone.CapstoneProject.Member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findById(Long id);
    Page<Board> findAll(Pageable pageable);
    @Query(value = "select * from free_board where  title LIKE CONCAT('%', :keyword, '%')" +
            " order by created_date desc",
            countQuery = "select count(*) from free_board where title LIKE CONCAT" +
                    "('%', :keyword, '%')",
            nativeQuery = true)
    Page<Board> fullTextSearch(@Param("keyword")String title, Pageable pageable);

    Page<Board> findByTitleContaining(@Param("keyword")String title, Pageable pageable);

   List<Board> findAllByWriter(Member userName);

}
