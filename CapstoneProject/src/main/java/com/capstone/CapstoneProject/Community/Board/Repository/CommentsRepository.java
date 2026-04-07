package com.capstone.CapstoneProject.Community.Board.Repository;

import com.capstone.CapstoneProject.Community.Board.Entity.Board;
import com.capstone.CapstoneProject.Community.Board.Entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllByPostId(Board postId);
}
