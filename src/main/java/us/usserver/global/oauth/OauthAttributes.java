package us.usserver.global.oauth;

import lombok.Builder;
import lombok.Getter;
import us.usserver.global.oauth.oauthEnum.SocialType;
import us.usserver.global.oauth.userinfo.GoogleOauth2UserInfo;
import us.usserver.global.oauth.userinfo.KakaoOauth2UserInfo;
import us.usserver.global.oauth.userinfo.NaverOauth2UserInfo;
import us.usserver.global.oauth.userinfo.Oauth2UserInfo;

import java.util.Map;

@Getter
public class OauthAttributes {
    private final String nameAttributeKey;
    private final Oauth2UserInfo oauth2UserInfo;

    @Builder
    public OauthAttributes(String nameAttributeKey, Oauth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OauthAttributes of(SocialType socialType, String userNameAttributeName, Map<String, Object> attributes) {
        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        } else if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        } else if (socialType == SocialType.GOOGLE) {
            return ofGoogle(userNameAttributeName, attributes);
        } else {
            throw new EnumConstantNotPresentException(SocialType.class, "올바른 Enum 값이 아닙니다.");
        }
    }

    public static OauthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOauth2UserInfo(attributes))
                .build();
    }
    private static OauthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOauth2UserInfo(attributes))
                .build();
    }
    private static OauthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OauthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOauth2UserInfo(attributes))
                .build();
    }
}
