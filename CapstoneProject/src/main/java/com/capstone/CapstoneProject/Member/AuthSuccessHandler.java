package com.capstone.CapstoneProject.Member;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                            throws IOException{
        CustomUser user = (CustomUser) authentication.getPrincipal();
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("nickname", user.getProfileName());
        response.setContentType("application/json; charset=UTF-8");

        new ObjectMapper().writeValue(response.getOutputStream(), responseBody);

            /*
            js에서 처리는
            axios.post("/login", loginData)
              .then(res => console.log(res.data.nickname));
            */

    }
}
