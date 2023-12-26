package us.usserver.global.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import us.usserver.global.jwt.TokenProvider;
import us.usserver.global.oauth.CustomOauth2User;
import us.usserver.member.Member;
import us.usserver.member.memberEnum.Role;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("Oauth2 Login Success!");
        Member member = ((CustomOauth2User) authentication.getPrincipal()).getMember();

        UriComponentsBuilder redirectURLBuilder = UriComponentsBuilder.fromUriString("http://localhost:8080/oauth2/login")
                .queryParam("email", member.getEmail())
                .queryParam("socialType", member.getSocialType())
                .queryParam("socialId", member.getSocialId())
                .queryParam("isAdult", member.getIsAdult())
                .queryParam("role", member.getRole())

                ;

        if (member.getRole().equals(Role.USER)) {
            String accessToken = tokenProvider.createAccessToken(member);
            String refreshToken = tokenProvider.createRefreshToken(member);

            tokenProvider.updateRefreshToken(String.valueOf(member.getId()), refreshToken);

            redirectURLBuilder
                    .queryParam("accessToken", "Bearer " + accessToken)
                    .queryParam("refreshToken", "Bearer" + refreshToken);
        }

        String redirectURL = redirectURLBuilder
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}
