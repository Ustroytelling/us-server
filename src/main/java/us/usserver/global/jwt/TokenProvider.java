package us.usserver.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import us.usserver.global.ExceptionMessage;
import us.usserver.global.RedisUtil;
import us.usserver.global.exception.MemberNotFoundException;
import us.usserver.global.exception.TokenInvalidException;
import us.usserver.member.Member;
import us.usserver.member.MemberRepository;

import java.util.Date;
import java.util.Optional;

import static us.usserver.global.ExceptionMessage.*;

@Getter
@RequiredArgsConstructor
@Slf4j
@Component
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String BEARER = "Bearer ";

    /**
     * AccessToken 생성
     */
    public String createAccessToken(Member member) {
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(Member member) {
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withClaim("id", member.getId())
                .withClaim("role", member.getRole().toString())
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /**
     * Request Header에서 Token 가져오기
     */
    public String extractToken(HttpServletRequest request, String tokenType) {
        Optional<String> requestToken = Optional.empty();

        if (tokenType.equals(ACCESS_TOKEN_SUBJECT)) {
            requestToken = Optional.ofNullable(request.getHeader(accessHeader))
                    .filter(token -> token.startsWith(BEARER))
                    .map(token -> token.substring(7));
        } else if (tokenType.equals(REFRESH_TOKEN_SUBJECT)) {
            requestToken = Optional.ofNullable(request.getHeader(refreshHeader))
                    .filter(token -> token.startsWith(BEARER))
                    .map(token -> token.substring(7));
        }

        return requestToken.orElse(null);
    }

    /**
     * redis <- refreshToken update
     */
    public void updateRefreshToken(String id, String refreshToken) {
        redisUtil.setDateExpire(id, refreshToken, refreshTokenExpirationPeriod);
    }

    /**
     * token verify
     */
    public DecodedJWT isTokenValid(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
        } catch (TokenExpiredException e) {
            log.error("token expired");
            throw new TokenInvalidException(Token_EXPIRED);
        } catch (JWTVerificationException e) {
            log.error("token verify fail");
            throw new TokenInvalidException(Token_VERIFICATION);
        } catch (Exception e) {
            throw new RuntimeException("Token Error!");
        }
    }

    /**
     * AccessToken 재발급
     */
    public String reissueToken(String refreshToken) {
        DecodedJWT decodedJWT = isTokenValid(refreshToken);
        Long id = decodedJWT.getClaim("id").asLong();
        String findToken = redisUtil.getData(String.valueOf(id));

        if (findToken == null) {
            throw new TokenInvalidException(Token_NOT_FOUND);
        } else if (!refreshToken.equals(findToken)) {
            throw new TokenInvalidException(Token_VERIFICATION);
        }

        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(Member_NOT_FOUND));
        return createAccessToken(member);
    }

    /**
     * token 유효 기간
     */
    public Long getExpiration(String token) {
        Date expiresAt = JWT
                .decode(token)
                .getExpiresAt();
        return expiresAt.getTime() - new Date().getTime();
    }

    public String renewToken(String refreshToken) {
        //request refreshToken -> User SocialId를 get -> redis refreshToken 유효한지 찾아서 검사
        DecodedJWT decodedJWT = isTokenValid(refreshToken);
        Long id = decodedJWT.getClaim("id").asLong();
        String findToken = redisUtil.getData(String.valueOf(id));

        if (findToken == null) {
            throw new TokenInvalidException(Token_NOT_FOUND);
        } else if (!findToken.equals(refreshToken)) {
            throw new TokenInvalidException(Token_VERIFICATION);
        }

        Member member = memberRepository.findById(id).orElseThrow(() -> new MemberNotFoundException(Member_NOT_FOUND));
        return createAccessToken(member);
    }

}
