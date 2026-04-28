package com.capstone.CapstoneProject.Member.Login.Oauth2;

import com.capstone.CapstoneProject.Member.Login.JWT.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    public OAuth2SuccessHandler(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String jwtToken = tokenProvider.createToken(authentication);

        String targetUrl = "http://localhost/oauth-redirect?token=" + jwtToken;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}
