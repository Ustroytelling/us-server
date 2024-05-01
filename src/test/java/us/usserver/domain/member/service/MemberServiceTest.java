package us.usserver.domain.member.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Transactional
@SpringBootTest
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;

    Member member;
    Author author;
    String accessToken;
    String refreshToken;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        member = memberRepository.save(member);

        accessToken = tokenProvider.issueAccessToken(member.getId());
        refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(1));
    }

    @Test
    @DisplayName("로그아웃 TEST")
    void logout() {
        // given

        // when
        memberService.logout(accessToken, refreshToken);

        // then
        assertNull(redisUtils.getData(member.getId().toString()));
    }

    @Test
    @DisplayName("회원 탈퇴 TEST")
    void withdraw() {
        // given

        // when
        memberService.withdraw(member.getId());
        Optional<Member> memberById = memberRepository.findById(member.getId());
        Optional<Author> authorById = authorRepository.findById(author.getId());

        // then
        assertTrue(memberById.isEmpty());
        assertTrue(authorById.isEmpty());
    }

    @Test
    @DisplayName("존재하지 않는 유저의 회원 탈퇴 TEST")
    void withdraw_2() {
        // given

        // when then
        assertThrows(BaseException.class,
                () -> memberService.withdraw(9999L));
    }
}