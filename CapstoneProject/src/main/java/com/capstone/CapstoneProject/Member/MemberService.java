package com.capstone.CapstoneProject.Member;

import com.capstone.CapstoneProject.Member.Login.Email.MailService;
import com.capstone.CapstoneProject.Member.Login.Email.MailServiceInterface;
import com.capstone.CapstoneProject.Member.Login.Email.VerificationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Component
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;
    private final MailServiceInterface mailServiceInterface;

    public void saveMember(MemberDTO memberDto) {
        Member member = memberDto.toEntity();
        List<String> roleNames = new ArrayList<>(); //권한 여러개 부여하기 위한 List
        roleNames.add("USER");                      //모든 유저는 USER 권한을 가짐
        if(memberDto.getUserId().equals("admin")) {
            roleNames.add("ADMIN");                 //이름이 admin일 경우 USER, ADMIN 두 개의 권한을 가짐
        }
        System.out.println(memberDto.getUserId());

        List<Authority> userRole = authorityRepository.findByNameIn(roleNames);
        Optional<Member> userID = memberRepository.findAllByUserID(member.getUserID());
        Optional<Member> profileName = memberRepository.findAllByProfileName(member.getProfileName());
        Optional<Member> email = memberRepository.findAllByEmail(member.getEmail());
        VerificationInfo info = mailServiceInterface.getVerificationInfo(memberDto.getEmail());
        System.out.println("memberService--------------" + memberDto.getEmail());
        System.out.println(info.getCode());
        System.out.println(info.isVerified());
        if(userID.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인아이디 입니다.");
        }else if (profileName.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        else if (email.isPresent()) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }else if(info == null || !info.isVerified()) {
            throw new IllegalArgumentException("이메일 인증 먼저 해주세요.");
        }
        member.getAuthorities().addAll(userRole);
        memberRepository.save(member);
        mailServiceInterface.removeVerification(memberDto.getEmail());
    }
    public boolean checkUserId(String userId) {
        Optional<Member> user = memberRepository.findAllByUserID(userId);
        if(user.isPresent()) {
            return false;
        }else{
            return true;
        }
    }
    public boolean checkNickname(String profileName) {
        Optional<Member> user = memberRepository.findAllByProfileName(profileName);
        if(user.isPresent()) {
            return false;
        }else{
            return true;
        }
    }
}

