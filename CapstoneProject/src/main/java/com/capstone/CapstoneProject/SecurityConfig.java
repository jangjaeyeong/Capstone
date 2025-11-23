package com.capstone.CapstoneProject;

import com.capstone.CapstoneProject.Member.AuthFailureHandler;
import com.capstone.CapstoneProject.Member.AuthSuccessHandler;
import jakarta.servlet.annotation.ServletSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ServletSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());   //Ngrok localhost 거부문제 개선
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((authorize) ->
                authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/**").permitAll());
        //Ngrok OPTION 문제 개선

        http.formLogin((form)
                -> form.loginProcessingUrl("/api/login")
                .usernameParameter("userID")
                .passwordParameter("userPassword")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler)
                
                //html 연결 시 formLogin -> formLogin.loginPage

        );
        http.logout(logout -> logout.logoutUrl("/logout"))
                .logout((logout) -> logout.logoutSuccessUrl("/mainPage"))
                .logout((logout) -> logout.invalidateHttpSession(true));

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

}

