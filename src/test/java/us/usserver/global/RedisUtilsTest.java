package us.usserver.global;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.global.utils.RedisUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Rollback
@Transactional
@SpringBootTest
public class RedisUtilsTest {
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MemberRepository memberRepository;

    private Member member;
    private Author author;

    @BeforeEach
    void setup() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("검색어 저장 TEST")
    void saveSearchLog_1() {
        // given
        String kw1 = "광마";

        // when
        redisUtils.saveSearchLog(kw1, member.getId());
        List<String> searchLogs = redisUtils.getSearchLogs(member.getId());

        // then
        assertEquals(1, searchLogs.size());
    }


    @Test
    @DisplayName("검색어 저장 10개 이상 TEST")
    void saveSearchLog_2() {
        // given
        String kw1 = "광마", kw2 = "회귀", kw3 = "소설 속", kw4 = "바바리안", kw5 = "살아남기", kw6 = "랜덤 검색어123", kw7 = "랜덤 검색어456", kw8 = "랜덤 검색어789", kw9 = "신의탑", kw10 = "투신", kw11 = "주술", kw12 = "회전";

        // when
        redisUtils.saveSearchLog(kw1, member.getId());
        redisUtils.saveSearchLog(kw2, member.getId());
        redisUtils.saveSearchLog(kw3, member.getId());
        redisUtils.saveSearchLog(kw4, member.getId());
        redisUtils.saveSearchLog(kw5, member.getId());
        redisUtils.saveSearchLog(kw6, member.getId());
        redisUtils.saveSearchLog(kw7, member.getId());
        redisUtils.saveSearchLog(kw8, member.getId());
        redisUtils.saveSearchLog(kw9, member.getId());
        redisUtils.saveSearchLog(kw10, member.getId());
        redisUtils.saveSearchLog(kw11, member.getId());
        redisUtils.saveSearchLog(kw12, member.getId());

        List<String> searchLogs = redisUtils.getSearchLogs(member.getId());


        // then
        assertEquals(10, searchLogs.size());
        searchLogs.forEach(searchLog -> assertThat(searchLog).doesNotContain("광마"));
        searchLogs.forEach(searchLog -> assertThat(searchLog).doesNotContain("회귀"));
    }

    @Test
    @DisplayName("검색어 중복 저장 TEST")
    void saveSearchLog_3() {
        // given
        String kw1 = "광마", kw2 = "회귀", kw3 = "회귀", kw4 = "회귀", kw5 = "회귀", kw6 = "회귀";

        // when
        redisUtils.saveSearchLog(kw1, member.getId());
        redisUtils.saveSearchLog(kw2, member.getId());
        redisUtils.saveSearchLog(kw3, member.getId());
        redisUtils.saveSearchLog(kw4, member.getId());
        redisUtils.saveSearchLog(kw5, member.getId());
        redisUtils.saveSearchLog(kw6, member.getId());

        List<String> searchLogs = redisUtils.getSearchLogs(member.getId());


        // then
        assertEquals(2, searchLogs.size());
        assertEquals("회귀", searchLogs.get(0));
        assertEquals("광마", searchLogs.get(1));
    }

    @Test
    @DisplayName("검색어 삭제 TEST")
    void deleteSearchLog_1() {
        // given
        String kw1 = "광마", kw2 = "회귀", kw3 = "게임 속", kw4 = "바바리안으로", kw5 = "살아남기", kw6 = "신의탑";

        // when
        redisUtils.saveSearchLog(kw1, member.getId());
        redisUtils.saveSearchLog(kw2, member.getId());
        redisUtils.saveSearchLog(kw3, member.getId());
        redisUtils.saveSearchLog(kw4, member.getId());
        redisUtils.saveSearchLog(kw5, member.getId());
        redisUtils.saveSearchLog(kw6, member.getId());
        redisUtils.deleteSearchLog(kw3, member.getId());
        List<String> searchLogs = redisUtils.getSearchLogs(member.getId());


        // then
        assertEquals(5, searchLogs.size());
        searchLogs.forEach(searchLog -> assertThat(searchLog).doesNotContain("게임 속"));
    }


    @Test
    @DisplayName("검색어 랭킹 조회 TEST")
    void getKeywordRanking() {
        // given
        String kw1 = "회귀", kw2 = "회귀", kw3 = "회귀", kw4 = "광마", kw5 = "광마";

        // when
        redisUtils.saveSearchLog(kw1, member.getId());
        redisUtils.saveSearchLog(kw2, member.getId());
        redisUtils.saveSearchLog(kw3, member.getId());
        redisUtils.saveSearchLog(kw4, member.getId());
        redisUtils.saveSearchLog(kw5, member.getId());
        List<String> ranking = redisUtils.getKeywordRanking();

        // then
        assertEquals("회귀", ranking.get(0));
        assertEquals("광마", ranking.get(1));
    }
}
