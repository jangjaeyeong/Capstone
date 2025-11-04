package com.capstone.CapstoneProject.Member;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

@Getter
@Setter
public class CustomUser extends User {

    public String profileName;
    public CustomUser(String username, String userpassword,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, userpassword, authorities);
    }
}
