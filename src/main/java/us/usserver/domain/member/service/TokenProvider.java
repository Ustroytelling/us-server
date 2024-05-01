package us.usserver.domain.member.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.stereotype.Component;
import us.usserver.domain.member.dto.token.TokenType;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class TokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;
    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;
    @Value("${jwt.refresh.duration}")
    private Long refreshTokenDuration;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String BEARER = "Bearer ";

    private final MemberRepository memberRepository;
    private final EntityFacade entityFacade;
    private final RedisUtils redisUtils;

    public String issueAccessToken(Long memberId) {
        return JWT.create()
                .withSubject(TokenType.ACCESS_TOKEN.getName())
                .withClaim("id", memberId)
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String issueRefreshToken(Long memberId) {
        return JWT.create()
                .withSubject(TokenType.REFRESH_TOKEN.getName())
                .withClaim("id", memberId)
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String reissueAccessToken(String refreshToken) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = JWT.require(Algorithm.HMAC512(secretKey)).build().verify(refreshToken);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(ExceptionMessage.TOKEN_VERIFICATION);
        }

        Long memberId = decodedJWT.getClaim("id").asLong();
        Long value = redisUtils.getData(refreshToken);

        if (memberId == null || value == null) {
            throw new AuthenticationServiceException(ExceptionMessage.TOKEN_NOT_FOUND);
        } else if (!memberId.equals(value)) {
            throw new AuthenticationServiceException(ExceptionMessage.TOKEN_VERIFICATION);
        }

        return issueAccessToken(memberId);
    }

    public void updateRefreshToken(String refreshToken, Long memberId) {
        redisUtils.setDateWithExpiration(refreshToken, memberId, Duration.ofDays(refreshTokenDuration));
    }

    public String extractToken(HttpServletRequest request, TokenType tokenType) {
        Optional<String> requestToken = Optional.empty();

        if (tokenType == TokenType.ACCESS_TOKEN) {
            requestToken = Optional.ofNullable(request.getHeader(accessHeader))
                    .filter(token -> token.startsWith(BEARER))
                    .map(token -> token.substring(7));
        } else if (tokenType == TokenType.REFRESH_TOKEN) {
            requestToken = Optional.ofNullable(request.getHeader(refreshHeader))
                    .filter(token -> token.startsWith(BEARER))
                    .map(token -> token.substring(7));
        }
        return requestToken.orElse(null);
    }

    public DecodedJWT decodeJWT(String accessToken, String refreshToken) {
        try {
            return JWT.require(Algorithm.HMAC512(secretKey)).build().verify(accessToken);
        } catch (TokenExpiredException e) {
            log.info("AccessToken is expired: ${}", accessToken);
            String newAccesstoken = reissueAccessToken(refreshToken);
            return decodeJWT(newAccesstoken, refreshToken);
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(ExceptionMessage.TOKEN_VERIFICATION);
        }
    }
}