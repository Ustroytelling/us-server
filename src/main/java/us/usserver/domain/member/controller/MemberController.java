package us.usserver.domain.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import us.usserver.domain.member.dto.token.TokenType;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.service.MemberService;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.global.response.ApiCsResponse;

@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {
    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @Operation(summary = "로그 아웃", description = "사용자 로그아웃 API")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    @PostMapping("/logout")
    public ApiCsResponse<Void> logoutMember(HttpServletRequest request) {
        String accessToken = tokenProvider.extractToken(request, TokenType.ACCESS_TOKEN);
        String refreshToken = tokenProvider.extractToken(request, TokenType.REFRESH_TOKEN);

        memberService.logout(accessToken, refreshToken);
        return ApiCsResponse.success();
    }

    @Operation(summary = "회원 탈퇴", description = "사용자 회원 탈퇴 API")
    @ApiResponse(responseCode = "200", description = "회원탈퇴 성공")
    @DeleteMapping("/withdraw")
    public ApiCsResponse<Void> withdrawMember(@AuthenticationPrincipal Long memberId) {
        memberService.withdraw(memberId);
        return ApiCsResponse.success();
    }
}
