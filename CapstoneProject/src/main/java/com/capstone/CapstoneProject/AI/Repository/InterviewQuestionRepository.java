package com.capstone.CapstoneProject.AI.Repository;

import com.capstone.CapstoneProject.AI.Entity.InterviewQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewQuestionRepository extends JpaRepository<InterviewQuestion, Long> {


}
