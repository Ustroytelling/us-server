package us.usserver.domain.member.dto.member;


import us.usserver.domain.member.constant.OauthProvider;

public sealed interface OauthMember permits AppleMember, KakaoMember, NaverMember {
    String getSocialId();

    String getEmail();

    String getNickname();

    OauthProvider getOauthProvider();
}
