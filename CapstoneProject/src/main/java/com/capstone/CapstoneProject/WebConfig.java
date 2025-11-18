package com.capstone.CapstoneProject;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) { //Ngrok 매핑
        registry.addMapping("/**")
                // allowedOrigins 대신 allowedOriginPatterns 사용
                .allowedOriginPatterns("http://localhost:3000", "https://*.ngrok-free.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}