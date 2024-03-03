package us.usserver.domain.member.dto.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import us.usserver.domain.member.constant.OauthProvider;
import us.usserver.domain.member.dto.member.GoogleMember;
import us.usserver.domain.member.dto.member.OauthMember;
import us.usserver.domain.member.dto.parameter.OauthParams;
import us.usserver.domain.member.dto.token.AppleToken;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;

@Slf4j
@Component
public class AppleClient implements OauthClient{
    @Value("${oauth.apple.token_url}")
    private String token_url;
    @Value("${oauth.apple.user_url}")
    private String user_url;
    @Value("${oauth.apple.grant_type}")
    private String grant_type;
    @Value("${oauth.apple.client_id}")
    private String client_id;
    @Value("${oauth.apple.redirect_uri}")
    private String redirect_uri;

    @Override
    public OauthProvider oauthProvider() {
        return OauthProvider.APPLE;
    }

    @Override
    public String getOauthLoginToken(OauthParams oauthParams) {
        String url = token_url;
        log.debug("전달할 code : " + oauthParams.getAuthorizationCode());

        // 요청 객체 생성
        RestTemplate rt = new RestTemplate();

        // 헤더 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디 생성
        MultiValueMap<String, String> body = oauthParams.makeBody();
        body.add("grant_type", grant_type);
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_uri);

        // 헤더 + 바디
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(body, httpHeaders);
        log.debug("현재 httpEntity 상태 : " + tokenRequest);

        // 토큰 수신
        AppleToken appleToken = rt.postForObject(url, tokenRequest, AppleToken.class);

        if (appleToken == null) {
            throw new BaseException(ErrorCode.TOKEN_VERIFICATION);
        }

        return appleToken.getAccess_token();
    }

    @Override
    public OauthMember getMemberInfo(String accessToken) {
        String url = user_url;
        log.debug("넘어온 token : " + accessToken);

        // 요청 객체 생성
        RestTemplate rt = new RestTemplate();

        // 헤더 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.add("Authorization", "Bearer " + accessToken);

        // 바디 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        // 헤더 + 바디
        HttpEntity<MultiValueMap<String, String>> memberInfoRequest = new HttpEntity<>(body, httpHeaders);

        return rt.postForObject(url, memberInfoRequest, GoogleMember.class);
    }
}
