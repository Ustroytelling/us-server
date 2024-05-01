package us.usserver.domain.member.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import us.usserver.domain.member.constant.Role;
import us.usserver.domain.member.constant.OauthProvider;

@Getter
@Builder
@AllArgsConstructor
public class LoginMemberResponse {
    @Schema(description = "사용자 email", example = "awfifwem123@kakao.com")
    @NotNull
    private String email;
    @Schema(description = "사용자 소셜 Type", example = "KAKAO, NAVER, GOOGLE")
    @NotNull
    private OauthProvider oauthProvider;
    @Schema(description = "사용자 소셜 ID", example = "12312312")
    @NotNull
    private String socialId;
    @Schema(description = "사용자 Role", example = "USER")
    @NotNull
    private Role role;
    @Schema(description = "accessToken", example = "nqwdodi12312392121nt35135y")
    @NotBlank
    private String accessToken;
    @Schema(description = "refreshToken", example = "1afweifweoiawneio12391723982")
    @NotBlank
    private String refreshToken;
}