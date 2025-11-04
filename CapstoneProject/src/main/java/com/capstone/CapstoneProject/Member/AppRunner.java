package com.capstone.CapstoneProject.Member;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {
    //jpa로 만든 엔터티 중 클라이언트에서 값을 받지 않는 엔터티에 속성값 넣어주는 놈

    public final AuthorityRepository authorityRepository;

    //생성자
    public AppRunner(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if(authorityRepository.findByName("USER").isEmpty()) {
            Authority userRole = new Authority();
            userRole.setName("USER");
            authorityRepository.save(userRole);
        }
        if(authorityRepository.findByName("ADMIN").isEmpty()) {
            Authority userRole = new Authority();
            userRole.setName("ADMIN");
            authorityRepository.save(userRole);
        }
    }
}
