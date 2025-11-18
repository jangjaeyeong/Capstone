package com.capstone.CapstoneProject.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;

    public void saveMember(MemberDTO memberDto) {
        Member member = memberDto.toEntity();
        List<String> roleNames = new ArrayList<>(); //권한 여러개 부여하기 위한 List
        roleNames.add("USER");                      //모든 유저는 USER 권한을 가짐
        if(memberDto.getName().equals("admin")) {
            roleNames.add("ADMIN");                 //이름이 admin일 경우 USER, ADMIN 두 개의 권한을 가짐
        }
        System.out.println(memberDto.getName());

        List<Authority> userRole = authorityRepository.findByNameIn(roleNames);
        Optional<Member> userID = memberRepository.findAllByUserID(member.getUserID());
        Optional<Member> profileName = memberRepository.findAllByProfileName(member.getProfileName());
        Optional<Member> cellphone = memberRepository.findAllByCellphone(member.getCellphone());

        if(userID.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인아이디 입니다.");
        }else if (profileName.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }else if (cellphone.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 전화번호입니다.");
        }
        member.getAuthorities().addAll(userRole);
        memberRepository.save(member);
    }
}

