package us.usserver.global.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import us.usserver.global.EntityService;
import us.usserver.global.exception.MemberNotFoundException;
import us.usserver.global.exception.TokenInvalidException;
import us.usserver.member.Member;
import us.usserver.member.MemberRepository;

import java.io.IOException;
import java.util.Collections;

import static us.usserver.global.ExceptionMessage.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final EntityService entityService;

    /**
     * 인증 하지 않는 페이지
     */
    private static final String[] whitelist = {
            "/", "/login**", "/oauth**", "/member/join",
            "/resources/**", "/favicon.ico", //resource
            "/swagger-ui/**", "/api-docs/**", //swagger
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return PatternMatchUtils.simpleMatch(whitelist, request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = tokenProvider.extractToken(request, "AccessToken");
        if (accessToken == null) {
            log.error(Token_NOT_FOUND);
            throw new TokenInvalidException(Token_NOT_FOUND);
        }

        //TODO: redis blacklist에 accessToken 값이 있는지(로그아웃을 한 사용자인지) 체크
        DecodedJWT decodedJWT = tokenProvider.isTokenValid(accessToken);
        Long id = decodedJWT.getClaim("id").asLong();
        Member member = entityService.getMember(id);
        Authentication authentication = new UsernamePasswordAuthenticationToken(member, null, Collections.singleton(new SimpleGrantedAuthority(member.getRole().toString())));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        doFilter(request, response, filterChain);
    }
}
