package com.capstone.CapstoneProject.AICalling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ToolsRepository extends JpaRepository<Tools, Integer> {

    @Query("select t.toolName from Tools t")
    List<String> findAllTools();
    Optional<Tools> findByToolName(String toolName);
}
