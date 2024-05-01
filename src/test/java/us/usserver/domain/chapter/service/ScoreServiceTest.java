package us.usserver.domain.chapter.service;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.chapter.dto.PostScore;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class ScoreServiceTest {
    @Autowired
    private ScoreService scoreService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    private Member member;
    private Author author;
    private Novel novel;
    private Chapter chapter;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
    }

    @Test
    @DisplayName("챕터 평가 하기")
    void postScore0() {
        // given
        PostScore postScore = PostScore.builder().score(10).build();

        // when
        assertDoesNotThrow(
                () -> scoreService.postScore(chapter.getId(), author.getId(), postScore));
    }

    @Test
    @DisplayName("범위를 벗어난 점수로 평가 하기")
    void postScore1() {
        // given
        int maxInt = 2147483647;
        int minInt = -2147483648;
        PostScore postScore1 = PostScore.builder().score(maxInt).build();
        PostScore postScore2 = PostScore.builder().score(minInt).build();

        // when then
        BaseException baseException1 = assertThrows(BaseException.class,
                () -> scoreService.postScore(chapter.getId(), author.getId(), postScore1));
        BaseException baseException2 = assertThrows(BaseException.class,
                () -> scoreService.postScore(chapter.getId(), author.getId(), postScore2));

        assertThat(baseException1.getMessage()).isEqualTo(ExceptionMessage.SCORE_OUT_OF_RANGE);
        assertThat(baseException2.getMessage()).isEqualTo(ExceptionMessage.SCORE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("챕터 중복 평가 하기")
    void postScore2() {
        // given
        PostScore postScore1 = PostScore.builder().score(10).build();
        PostScore postScore2 = PostScore.builder().score(1).build();

        // when
        assertDoesNotThrow(
                () -> scoreService.postScore(chapter.getId(), author.getId(), postScore1));
        BaseException baseException = assertThrows(BaseException.class,
                () -> scoreService.postScore(chapter.getId(), author.getId(), postScore2));


        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.SCORE_ALREADY_ENTERED);
    }


    @Test
    @DisplayName("챕터 평점 조회 하기")
    void getChapterScore() {
        // given
        Member member1 = MemberMother.generateMember();
        Author author1 = AuthorMother.generateAuthorWithMember(member1);
        member1.setAuthor(author1);
        memberRepository.save(member1);

        Member member2 = MemberMother.generateMember();
        Author author2 = AuthorMother.generateAuthorWithMember(member2);
        member2.setAuthor(author2);
        memberRepository.save(member2);

        Member member3 = MemberMother.generateMember();
        Author author3 = AuthorMother.generateAuthorWithMember(member3);
        member3.setAuthor(author3);
        memberRepository.save(member3);

        PostScore postScore1 = PostScore.builder().score(1).build();
        PostScore postScore2 = PostScore.builder().score(3).build();
        PostScore postScore3 = PostScore.builder().score(10).build();

        // when
        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        scoreService.postScore(chapter.getId(), author1.getId(), postScore1);
        scoreService.postScore(chapter.getId(), author2.getId(), postScore2);
        scoreService.postScore(chapter.getId(), author3.getId(), postScore3);
        Double chapterScore = scoreService.getChapterScore(chapter);

        // then
        assertThat(chapterScore).isCloseTo(4.666666666666667, Percentage.withPercentage(0.1));
    }
}