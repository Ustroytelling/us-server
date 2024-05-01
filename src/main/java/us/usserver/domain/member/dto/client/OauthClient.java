package us.usserver.domain.member.dto.client;

import us.usserver.domain.member.dto.member.OauthMember;
import us.usserver.domain.member.dto.parameter.OauthParams;
import us.usserver.domain.member.constant.OauthProvider;

public interface OauthClient {
    public OauthProvider oauthProvider();

    public String getOauthLoginToken(OauthParams oauthParams);

    public OauthMember getMemberInfo(String accessToken);
}
