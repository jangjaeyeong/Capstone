package com.capstone.CapstoneProject.Member.Login;

import com.capstone.CapstoneProject.Member.Member;
import com.capstone.CapstoneProject.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MyUserDetailService implements UserDetailsService {
    public final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<Member> member = memberRepository.findAllByUserID(userId);

        if (member.isEmpty()) {
            throw new UsernameNotFoundException("일치하는 아이디가 없습니다.");
        }
        Member user = member.get();
        List<GrantedAuthority> authorities = user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .collect(Collectors.toList());

        CustomUser customUser = new CustomUser(user.getUserID(), user.getPassword(), authorities);
        customUser.setProfileName(user.getProfileName());
        return customUser;

    }
}