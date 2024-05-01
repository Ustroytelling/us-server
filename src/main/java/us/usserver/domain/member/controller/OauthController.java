package us.usserver.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.member.dto.LoginDto;
import us.usserver.domain.member.dto.MemberInfoDto;
import us.usserver.domain.member.dto.parameter.AppleParams;
import us.usserver.domain.member.dto.parameter.KakaoParams;
import us.usserver.domain.member.dto.parameter.NaverParams;
import us.usserver.domain.member.dto.req.OauthRequest;
import us.usserver.domain.member.dto.token.TokenType;
import us.usserver.domain.member.service.OauthService;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.global.response.ApiCsResponse;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/oauth2")
@RestController
public class OauthController {
    private final TokenProvider tokenProvider;
    private final OauthService oauthService;

    @Operation(summary = "카카오 소셜 로그인", description = "로그인 API \n\n" +
            "인증 Code를 전달받아서 Social Server와 통신하며 사용자 정보를 받아오는 로직 \n\n \n\n" +
            "로직 설명 \n\n" +
            "1. Social 인증 Code를 전달받는다. \n\n" +
            "2. 인증 Code를 활용하여 accessToken 요청 URI를 작성한뒤 social server에 요청을 보내 accessToken을 받는다. \n\n" +
            "3. 받은 accessToken을 활용하여 사용자 정보 요청 URI를 작성한 뒤 요청을 보낸다. \n\n" +
            "4. 사용자 정보를 DB에서 찾아 신규, 기존 유저인지 판별 후 해당하는 값들을 response \n\n" +
            "※ 신규 유저는 userId와 role(GUEST)를 기존 유저는 userId와 role(USER), accessToken, refreshToken을 발급하여 Response ※"
    )
    @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginDto.class)))
    @PostMapping("/login")
    public ResponseEntity<LoginDto> socialLogin(@RequestBody OauthRequest oauthRequest) {
        log.debug("넘겨받은 kakao 인증키 :: " + oauthRequest.getCode());

        MemberInfoDto memberInfoDto = switch (oauthRequest.getOauthProvider()){
            case KAKAO -> oauthService.getMemberByOauthLogin(new KakaoParams(oauthRequest.getCode()));
            case NAVER ->
                    oauthService.getMemberByOauthLogin(new NaverParams(oauthRequest.getCode(), oauthRequest.getState()));
            case APPLE -> oauthService.getMemberByOauthLogin(new AppleParams(oauthRequest.getCode()));
        };

        //응답 헤더 생성
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(tokenProvider.getAccessHeader(), memberInfoDto.accessToken());
        httpHeaders.add(tokenProvider.getRefreshHeader(), memberInfoDto.refreshToken());
        return ResponseEntity.ok().headers(httpHeaders).body(new LoginDto(memberInfoDto.memberId()));
    }

    @ApiResponse(responseCode = "201", description = "토큰 재발급 API")
    @GetMapping("/renew-token")
    public ApiCsResponse<Void> renewToken(HttpServletRequest request, HttpServletResponse servletResponse) {
        String refreshToken = tokenProvider.extractToken(request, TokenType.REFRESH_TOKEN);
        String accessToken = tokenProvider.reissueAccessToken(refreshToken);

        servletResponse.addHeader(tokenProvider.getAccessHeader(), "Bearer " + accessToken);
        return ApiCsResponse.success();
    }
}