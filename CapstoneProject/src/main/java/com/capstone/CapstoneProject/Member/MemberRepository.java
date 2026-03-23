package com.capstone.CapstoneProject.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findAllByUserID(String userId);
    Optional<Member> findAllByCellphone(String cellphone);
    Optional<Member> findAllByProfileName(String profileName);
    Member findByUserID(String userID);

    Optional<Member> findByProfileName(String userId);
}
