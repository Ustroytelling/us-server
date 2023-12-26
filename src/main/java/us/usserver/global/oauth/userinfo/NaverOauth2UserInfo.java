package us.usserver.global.oauth.userinfo;

import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo{

    public NaverOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        return getResponseMap("name");
    }

    @Override
    public String getSocialId() {
        return getResponseMap("id");

    }

    @Override
    public String getEmail() {
        return getResponseMap("email");
    }

    @Override
    public Boolean getAge() {
            String ageRange = getResponseMap("age");
        return ageRange.startsWith("20");
    }

    @Override
    public String getImageUrl() {
        return getResponseMap("profile_image");
    }

    private String getResponseMap(String key) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return String.valueOf(response.get(key));
    }
}
