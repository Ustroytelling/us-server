package us.usserver.global.oauth.userinfo;

import java.util.Map;

public class GoogleOauth2UserInfo extends Oauth2UserInfo{


    public GoogleOauth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getName() {
        return String.valueOf(attributes.get("name"));
    }

    @Override
    public String getSocialId() {
        return String.valueOf(attributes.get("sub"));
    }

    @Override
    public String getEmail() {
        return String.valueOf(attributes.get("email"));
    }

    @Override
    public Boolean getAge() {
        //TODO: 구글의 경우 연령대 혹은 생일을 지원하지 않고 있어서 추가로 인증이 필요!
        // 대부분의 플랫폼에서 사용하는 본인인증은 NICE의 API를 사용하고 있어서 유료
        // 추후에 논의 후 수정
        return null;
    }

    @Override
    public String getImageUrl() {
        return String.valueOf(attributes.get("picture"));
    }
}
