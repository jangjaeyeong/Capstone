package com.capstone.CapstoneProject.Member.Login.Oauth2.DTO;
import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");

        if (kakaoAccount != null && kakaoAccount.get("profile") != null) {
            this.profile = (Map<String, Object>) kakaoAccount.get("profile");
        } else {
            this.profile = null;
        }
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        // 이메일 동의를 안 해서 이메일이 안 들어올 수도 있습니다! 방어 코드를 짭니다.
        if (kakaoAccount == null || kakaoAccount.get("email") == null) {
            // 이메일이 없으면 임시로 "고유번호@kakao.com" 형식으로 만들어줍니다.
            return getProviderId() + "@kakao.com";
        }
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        // 닉네임 동의를 안 했거나 프로필 상자가 통째로 안 들어왔을 때!
        if (profile == null || profile.get("nickname") == null) {
            // 닉네임이 없으면 임시로 "User_고유번호" 형식으로 만들어줍니다.
            return "User_" + getProviderId();
        }
        return profile.get("nickname").toString();
    }
}