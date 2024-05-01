package us.usserver.domain.member.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.usserver.domain.member.constant.OauthProvider;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OauthRequest {
    @Schema(description = "소셜 타입", example = "NAVER or KAKAO")
    @NotNull
    private OauthProvider oauthProvider;

    @Schema(description = "소셜 인증 Code", example = "213afiiwqjd2")
    @NotNull
    private String code;

    @Schema(description = "요청 위조를 방조하기 위한 상태 토큰 값", nullable = true, example = "jd123")
    private String state;
}
