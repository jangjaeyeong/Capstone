package com.capstone.CapstoneProject.Community;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Component
public class CommonService {

    public void checkUser(UserDetails user) {
        if (user == null) {
            throw new RuntimeException("회원 정보가 없습니다.");
        }
    }

}
