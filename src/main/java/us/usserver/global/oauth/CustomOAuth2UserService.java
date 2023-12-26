package us.usserver.global.oauth;

import com.fasterxml.classmate.MemberResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import us.usserver.global.oauth.oauthEnum.SocialType;
import us.usserver.global.oauth.userinfo.Oauth2UserInfo;
import us.usserver.member.Member;
import us.usserver.member.MemberRepository;
import us.usserver.member.memberEnum.Gender;
import us.usserver.member.memberEnum.Role;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private static final String KAKAO = "kakao";
    private static final String NAVER = "naver";
    private static final String GOOGLE = "google";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        /**
         * Oauth server 에서 가져온 user 정보를 Get
         */
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /**
         * registrationId: socialType -> kakao or naver or google
         * userNameAttributeName: attributes primary key
         * attributes: user info가 담겨있는 data
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OauthAttributes oauth2Attributes = OauthAttributes.of(socialType, userNameAttributeName, attributes);
        Oauth2UserInfo oauth2UserInfo = oauth2Attributes.getOauth2UserInfo();
        String socialId = oauth2UserInfo.getSocialId();
        String email = oauth2UserInfo.getEmail();
        Boolean isAdult = oauth2UserInfo.getAge();

        Member member = getMember(socialId, email, socialType, isAdult);

        return new CustomOauth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().toString())),
                attributes,
                oauth2Attributes.getNameAttributeKey(),
                member
        );
    }

    private Member getMember(String socialId, String email, SocialType socialType, Boolean isAdult) {
        return memberRepository.findBySocialTypeAndSocialId(socialType, socialId)
                .orElse(
                        Member.builder()
                                .socialId(socialId)
                                .socialType(socialType)
                                .email(email)
                                .age(-1)
                                .gender(Gender.UNKNOWN)
                                .isAdult(isAdult != null ? isAdult : false)
                                .role(Role.GUEST)
                                .build()
                );
    }

    private SocialType getSocialType(String registrationId) {
        if (KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        } else if (NAVER.equals(registrationId)) {
            return SocialType.NAVER;
        } else if (GOOGLE.equals(registrationId)) {
            return SocialType.GOOGLE;
        } else {
            throw new EnumConstantNotPresentException(SocialType.class, "올바른 Enum 값이 아닙니다.");
        }
    }
}
