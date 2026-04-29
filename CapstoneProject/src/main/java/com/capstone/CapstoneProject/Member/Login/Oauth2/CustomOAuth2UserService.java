package com.capstone.CapstoneProject.Member.Login.Oauth2;

import com.capstone.CapstoneProject.Member.AuthorityRepository;
import com.capstone.CapstoneProject.Member.Login.Oauth2.DTO.KakaoResponse;
import com.capstone.CapstoneProject.Member.Login.Oauth2.DTO.NaverResponse;
import com.capstone.CapstoneProject.Member.Login.Oauth2.DTO.OAuth2Response;
import com.capstone.CapstoneProject.Member.Member;
import com.capstone.CapstoneProject.Member.MemberDTO;
import com.capstone.CapstoneProject.Member.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final AuthorityRepository authorityRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository, AuthorityRepository authorityRepository) {
        this.memberRepository = memberRepository;
        this.authorityRepository = authorityRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("============= loadUser 진입 성공! =============");
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (provider.equals("kakao")) {
            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        } else if (provider.equals("naver")) {
             oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            System.out.println("네이버에서 받아온 데이터: " + oAuth2User.getAttributes());
        }


        String email = oAuth2Response.getEmail();
        String nickname = oAuth2Response.getName();
        String providerId = oAuth2Response.getProvider();
        String providerCode = oAuth2Response.getProviderId();
        Member member = memberRepository.findByProviderCode(providerCode);
        MemberDTO memberDTO = new MemberDTO();
        System.out.println("이메일-------" + email);
        System.out.println("닉네임-------" + nickname);
        System.out.println("소셜 로그인 출처-------" + providerId);
        System.out.println("소셜로그인 고유 ID-------" + providerCode);
        if (member == null) {
            member = Member.builder()
                    .email(email)
                    .profileName(nickname)
                    .provider(provider)
                    .providerCode(providerCode)
                    .build();
            memberRepository.save(member);

        } else {
           memberDTO.updateSocialInfo(nickname, email);
            memberRepository.save(member);
        }

        return new CustomOAuth2User(member, authorityRepository);
    }
    }
