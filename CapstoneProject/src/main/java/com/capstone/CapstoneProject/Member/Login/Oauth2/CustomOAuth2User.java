package com.capstone.CapstoneProject.Member.Login.Oauth2;

import com.capstone.CapstoneProject.Member.Authority;
import com.capstone.CapstoneProject.Member.AuthorityRepository;
import com.capstone.CapstoneProject.Member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final Member member;
    private final AuthorityRepository authorityRepository;

    public CustomOAuth2User(Member member, AuthorityRepository authorityRepository) {
        this.member = member;
        this.authorityRepository = authorityRepository;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(authorityRepository.findById(member.getId()).toString()));
        return collection;
    }

    //유저의 닉네임을 알려주는 메서드
    @Override
    public String getName() {
        return member.getProfileName();
    }

    //소셜 서버에서 받은 원본 데이터
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    public String getEmail() {
        return member.getEmail();
    }
     public String getRole() {
        return authorityRepository.findById(member.getId()).toString();
     }

}