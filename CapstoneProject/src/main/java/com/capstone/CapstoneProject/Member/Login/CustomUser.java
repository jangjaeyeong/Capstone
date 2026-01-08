package com.capstone.CapstoneProject.Member.Login;

import com.capstone.CapstoneProject.Member.Member;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collections;

@Getter
@Setter
public class CustomUser extends User {
    private Member member;
    public String profileName;
    public CustomUser(Member member) {
        super(member.getUserID(), member.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority("USER")));

        this.member = member;
    }
    public Member getMember() {
        return member;
    }
}
