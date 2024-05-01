package us.usserver.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.member.constant.Gender;
import us.usserver.domain.member.constant.Role;
import us.usserver.domain.member.dto.MemberInfoDto;
import us.usserver.domain.member.dto.member.OauthMember;
import us.usserver.domain.member.dto.parameter.OauthParams;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class OauthService {
    private final RequestOauthInfoService requestOauthInfoService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final RedisUtils redisUtils;

    @Value("${jwt.refresh.duration}")
    private int expiration;

    public final String[] NICKNAME = {"하늘", "구름", "숲속", "들판", "우주", "바다", "물결", "별빛", "소망", "노을",
            "노래", "햇살", "연목", "바람", "동산", "미지", "모험", "탐험"};

    @Transactional
    public MemberInfoDto getMemberByOauthLogin(OauthParams oauthParams) {
        OauthMember request = requestOauthInfoService.request(oauthParams);

        Optional<Member> bySocialTypeAndSocialId = memberRepository.findByOauthProviderAndSocialId(request.getOauthProvider(), request.getSocialId());
        if (bySocialTypeAndSocialId.isPresent()) {
            return getMemberInfoDto(bySocialTypeAndSocialId);
        }
        else {
            return getNewMemberInfoDto(request);
        }
    }

    private MemberInfoDto getNewMemberInfoDto(OauthMember request) {
        Member newMember = Member.createMemberInSocialLogin(
                request.getSocialId(), request.getOauthProvider(), request.getEmail(), 0, Gender.UNKNOWN, Role.USER);
        Author newAuthor = Author.createAuthorInSocialLogin(newMember.getId(), newMember, getRandomNickname());
        newMember.setAuthor(newAuthor);

        newMember = memberRepository.save(newMember);

        String accessToken = tokenProvider.issueAccessToken(newMember.getId());
        String refreshToken = tokenProvider.issueRefreshToken(newMember.getId());
        redisUtils.setDateWithExpiration(refreshToken, newMember.getId(), Duration.ofDays(expiration));
        return new MemberInfoDto(newMember.getId(), newMember.getRole(), accessToken, refreshToken);
    }

    private String getRandomNickname() {
        int number = ThreadLocalRandom.current().nextInt(0, 999);
        return NICKNAME[ThreadLocalRandom.current().nextInt(18)] + number;
    }

    private MemberInfoDto getMemberInfoDto(Optional<Member> bySocialTypeAndSocialId) {
        Member member = bySocialTypeAndSocialId.get();

        String accessToken = tokenProvider.issueAccessToken(member.getId());
        String refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(expiration));
        return new MemberInfoDto(member.getId(), member.getRole(), accessToken, refreshToken);
    }
}
