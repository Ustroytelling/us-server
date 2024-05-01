package us.usserver.domain.member.dto.parameter;

import org.springframework.util.MultiValueMap;
import us.usserver.domain.member.constant.OauthProvider;

public sealed interface OauthParams permits AppleParams, KakaoParams, NaverParams {
    OauthProvider oauthProvider();
    String getAuthorizationCode();
    MultiValueMap<String, String> makeBody();
}
