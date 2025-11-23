package com.capstone.CapstoneProject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
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

    @Configuration // "이건 설정 파일이야"
    public class RestTemplateConfig {

        @Bean // "이 메서드가 반환하는 걸 스프링 빈으로 등록해줘"
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }
    }
}