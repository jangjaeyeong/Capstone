package com.capstone.CapstoneProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {
    Optional<Authority> findByName(String name);
    List<Authority> findByNameIn(List<String> names);

}
