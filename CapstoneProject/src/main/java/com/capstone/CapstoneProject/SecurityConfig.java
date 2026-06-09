package com.capstone.CapstoneProject;

import com.capstone.CapstoneProject.Member.Login.*;
import com.capstone.CapstoneProject.Member.Login.JWT.JwtAccessDeniedHandler;
import com.capstone.CapstoneProject.Member.Login.JWT.JwtAuthenticationEntryPoint;
import com.capstone.CapstoneProject.Member.Login.JWT.JwtFilter;
import com.capstone.CapstoneProject.Member.Login.JWT.TokenProvider;
import com.capstone.CapstoneProject.Member.Login.Oauth2.CustomOAuth2UserService;
import com.capstone.CapstoneProject.Member.Login.Oauth2.OAuth2SuccessHandler;
import jakarta.servlet.annotation.ServletSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@ServletSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //Ngrok localhost 거부문제 개선
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers("/api/auth/login", "/api/auth/signup").permitAll()
                                .requestMatchers(HttpMethod.GET, "/**").permitAll()
                                .requestMatchers("/api/auth/email/send-code",
                                        "/api/auth/email/verify-code").permitAll()
                                .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/api/oauth2/authorization"))
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/api/login/oauth2/code/*"))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2SuccessHandler));
        http.cors(Customizer.withDefaults());
        //Ngrok OPTION 문제 개선
        http.exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(new JwtFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

//        http.authorizeHttpRequests(auth -> auth
//                        // 이 부분이 중요! /css/**, /js/** 등 정적 리소스는 모두 허용
//                        .requestMatchers("/static/CSS/**", "/static/js/**", "/static/images/**").permitAll()
//                        .anyRequest().authenticated()
//                );
        return http.build();
    }

    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

        private TokenProvider tokenProvider;

        public JwtSecurityConfig(TokenProvider tokenProvider) {
            this.tokenProvider = tokenProvider;
        }

        // Security로직에 필터를 등록
        @Override
        public void configure(HttpSecurity http) {
            JwtFilter customFilter = new JwtFilter(tokenProvider);
            http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 프론트엔드 주소 허용 (포트 번호 주의!)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://provi.kr", "https://www.provi.kr"));
        // ⭐️ DELETE, PUT 등 모든 방식 허용
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Authorization 헤더 허용
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;

    }
}


