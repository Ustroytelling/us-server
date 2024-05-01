package us.usserver.domain.member.service;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.member.dto.token.TokenType;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;
import us.usserver.global.utils.RedisUtils;

import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final RedisUtils redisUtils;
    private final MemberRepository memberRepository;
    private final AuthorRepository authorRepository;
    private final TokenProvider tokenProvider;

    @Override
    public void logout(String accessToken, String refreshToken) {
        DecodedJWT decodedJWT = tokenProvider.decodeJWT(accessToken, refreshToken);
        Long memberId = decodedJWT.getClaim("id").asLong();
        String strMemberId = memberId.toString();

        if (redisUtils.getData(strMemberId) != null)
            redisUtils.deleteData(strMemberId);
    }

    @Override
    public void withdraw(Long memberId) {
        authorRepository.getAuthorByMemberId(memberId)
                .orElseThrow(() -> new BaseException(ErrorCode.AUTHOR_NOT_FOUND));

        String strMemberId = memberId.toString();
        redisUtils.deleteData(strMemberId);
        memberRepository.deleteById(memberId);
    }
}
