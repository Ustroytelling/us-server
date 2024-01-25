package us.usserver.global.oauth;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.usserver.global.ApiCsResponse;
import us.usserver.global.exception.AuthorNotFoundException;
import us.usserver.global.exception.TokenInvalidException;
import us.usserver.global.jwt.TokenProvider;
import us.usserver.member.Member;
import us.usserver.member.MemberService;
import us.usserver.member.dto.LoginMemberResponse;
import us.usserver.member.memberEnum.Gender;
import us.usserver.member.memberEnum.Role;


@RequiredArgsConstructor
@RequestMapping("/oauth2")
@RestController
public class OauthController {
    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    @GetMapping("/login")
    public ResponseEntity<ApiCsResponse<?>> loadOAuthLogin(HttpServletResponse servletResponse,
                                                           @ModelAttribute LoginMemberResponse loginMemberResponse) {
        Member member = null;

        if (loginMemberResponse.getRole().equals(Role.GUEST)) {
            member = Member.builder()
                    .id(-1L)
                    .socialId(loginMemberResponse.getSocialId())
                    .socialType(loginMemberResponse.getSocialType())
                    .email(loginMemberResponse.getEmail())
                    .age(-1)
                    .gender(Gender.UNKNOWN)
                    .role(loginMemberResponse.getRole())
                    .build();

        } else if (loginMemberResponse.getRole().equals(Role.USER)) {
            member = memberService.getMyInfo(loginMemberResponse.getSocialId());

            servletResponse.addHeader(tokenProvider.getAccessHeader(), loginMemberResponse.getAccessToken());
            servletResponse.addHeader(tokenProvider.getRefreshHeader(), loginMemberResponse.getRefreshToken());
        }

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(member)
                .build();

        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "토큰 재발급 API"),
            @ApiResponse(responseCode = "400", description = "토큰을 찾을 수 없습니다",
                    content = @Content(schema = @Schema(implementation = TokenInvalidException.class)))
    })
    @GetMapping("/renew-token")
    public ResponseEntity<ApiCsResponse<?>> renewToken(HttpServletRequest request, HttpServletResponse servletResponse) {
        String refreshToken = tokenProvider.extractToken(request, "RefreshToken");
        String accessToken = tokenProvider.renewToken(refreshToken);
        servletResponse.addHeader(tokenProvider.getAccessHeader(), "Bearer " + accessToken);
        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}