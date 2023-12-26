package us.usserver.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import us.usserver.global.oauth.oauthEnum.SocialType;
import us.usserver.member.memberEnum.Role;

@Getter
@Builder
@AllArgsConstructor
public class
LoginMemberResponse {
    @NotNull
    private String email;
    @NotNull
    private SocialType socialType;
    @NotNull
    private String socialId;
    @NotNull
    private Boolean isAdult;
    @NotNull
    private Role role;
    private String accessToken;
    private String refreshToken;
}
