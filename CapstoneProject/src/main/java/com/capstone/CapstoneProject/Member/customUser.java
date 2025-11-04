package com.capstone.CapstoneProject.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import java.util.Collection;

public class customUser extends User {

    public String profileName;
    public customUser(String username, String userpassword,
                      Collection<? extends GrantedAuthority> authorities) {
        super(username, userpassword, authorities);
    }
}
