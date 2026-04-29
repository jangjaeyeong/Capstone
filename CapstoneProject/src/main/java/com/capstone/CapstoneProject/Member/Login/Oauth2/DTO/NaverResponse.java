package com.capstone.CapstoneProject.Member.Login.Oauth2.DTO;

import java.util.HashMap;
import java.util.Map;

public class NaverResponse implements OAuth2Response {
         Map<String, Object> attribute;

        public NaverResponse(Map<String, Object> attribute) {
            if(attribute == null ) {
                this.attribute = new HashMap<>();
                return;
            }
            this.attribute = (Map<String, Object>) attribute.get("response");
            if (this.attribute == null) {
                this.attribute = new HashMap<>();
            }
        }
    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Object emailObj = attribute.get("email");
        if (emailObj == null) {
            // 네이버가 이메일을 안 줬다면? 에러 내지 말고 고유 식별자로 가짜 이메일을 만들어서 던져준다!
            return getProviderId() + "@naver.com";
        }

        return emailObj.toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
