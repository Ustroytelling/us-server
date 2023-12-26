package us.usserver.global.oauth.userinfo;

import com.nimbusds.oauth2.sdk.util.MapUtils;

import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo{
    public KakaoOauth2UserInfo(Map<String, Object> attributes){
        super(attributes);
    }

    @Override
    public String getName() {
        return getAccountMap("name", null);
    }

    @Override
    public String getSocialId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return getAccountMap("email", null);
    }

    @Override
    public Boolean getAge() {
        String ageRange = getAccountMap("age_range", null);
        assert ageRange != null;

        return ageRange.startsWith("20");
    }

    @Override
    public String getImageUrl() {
        return getAccountMap("profile", "thumbnail_image_url");
    }

    private String getAccountMap(String key1, String key2) {
        Map<String, Object> accountMap = (Map<String, Object>) attributes.get("kakao_account");

        if (accountMap == null) {
            return null;
        }

        if (key2 == null) {
            return String.valueOf(accountMap.get(key1));
        }
        Map<String, Object> KeyMap = (Map<String, Object>) accountMap.get(key1);
        return String.valueOf(KeyMap.get(key2));
    }
}
