package com.capstone.CapstoneProject.Member.Login;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthFailureHandler implements AuthenticationFailureHandler {
        ObjectMapper mapper = new ObjectMapper();
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
                                            throws IOException{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=utf-8");
        Map<String, String> errMsg =  new HashMap<>();
        if(exception instanceof BadCredentialsException) {
            errMsg.put("Message", "아이디 혹은 비밀번호가 틀렸습니다.");
        }else {
            errMsg.put("Message", "로그인 중 오류가 발생했습니다.");
            log.error("Unknown login error", exception);
        }

        mapper.writeValue(response.getWriter(), errMsg);
    }
}
