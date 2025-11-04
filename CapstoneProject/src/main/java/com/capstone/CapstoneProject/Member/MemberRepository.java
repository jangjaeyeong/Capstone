package com.capstone.CapstoneProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findAllByUserID(String userId);
    Optional<Member> findAllByCellphone(String cellphone);
    Optional<Member> findAllByProfileName(String profileName);
}
