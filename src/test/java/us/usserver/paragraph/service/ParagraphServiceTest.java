package us.usserver.paragraph.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import us.usserver.domain.author.entity.Author;
import us.usserver.author.AuthorMother;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.chapter.ChapterMother;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.chapter.constant.ChapterStatus;
import us.usserver.domain.paragraph.service.ParagraphService;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.global.response.exception.MainAuthorIsNotMatchedException;
import us.usserver.global.response.exception.ParagraphLengthOutOfRangeException;
import us.usserver.domain.member.entity.Member;
import us.usserver.member.MemberMother;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.novel.NovelMother;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.dto.res.GetParagraphResponse;
import us.usserver.domain.paragraph.dto.ParagraphInVoting;
import us.usserver.domain.paragraph.dto.ParagraphsOfChapter;
import us.usserver.domain.paragraph.dto.req.PostParagraphReq;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.VoteRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
class ParagraphServiceTest {
    @Autowired
    private ParagraphService paragraphService;

    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private VoteRepository voteJpaRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    private Author author;
    private Novel novel;
    private Chapter chapter;
    private Paragraph paragraph1;
    private Paragraph paragraph2;

    @BeforeEach
    public void setUp() {
        author = AuthorMother.generateAuthor();
        setMember(author);
        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        paragraph1 = ParagraphMother.generateParagraph(author, chapter);
        paragraph2 = ParagraphMother.generateParagraph(author, chapter);
        paragraph1.setSequenceForTest(0);
        paragraph2.setSequenceForTest(0);
        chapter.getParagraphs().add(paragraph1);
        chapter.getParagraphs().add(paragraph2);

        novel.getChapters().add(chapter);

        authorRepository.save(author);
        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
    }

    @Test
    @DisplayName("회차 보기")
    void getParagraphs() {
        // given

        // when
        ParagraphsOfChapter paragraphs = paragraphService.getParagraphs(author.getId(), chapter.getId());

        // then
        assertNotNull(paragraphs.getMyParagraph());
        assertNull(paragraphs.getBestParagraph());
        assertThat(paragraphs.getSelectedParagraphs()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("어떤 한줄도 등록되지 않은 회차 보기")
    void getInitialChParagraph() {
        // given
        Chapter SecondChapter = ChapterMother.generateChapter(novel);

        // when
        novel.getChapters().add(SecondChapter);
        chapterRepository.save(SecondChapter);
        ParagraphsOfChapter paragraphs = paragraphService.getParagraphs(author.getId(), SecondChapter.getId());

        // then
        assertNull(paragraphs.getMyParagraph());
        assertNull(paragraphs.getBestParagraph());
        assertThat(paragraphs.getSelectedParagraphs()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("내가 쓴 한줄은 없고, 베스트 한줄은 있는 회차 보기")
    void getParagraph0() {
        // given
        Author author1 = AuthorMother.generateAuthor();
        Author author2 = AuthorMother.generateAuthor();
        Author author3 = AuthorMother.generateAuthor();
        setMember(author1);
        setMember(author2);
        setMember(author3);

        Vote like1 = Vote.builder().paragraph(paragraph1).author(author1).build();
        Vote like2 = Vote.builder().paragraph(paragraph1).author(author2).build();
        Vote like3 = Vote.builder().paragraph(paragraph1).author(author3).build();
        Vote like4 = Vote.builder().paragraph(paragraph2).author(author3).build();

        // when
        authorRepository.save(author1);
        authorRepository.save(author2);
        authorRepository.save(author3);
        voteJpaRepository.save(like1);
        voteJpaRepository.save(like2);
        voteJpaRepository.save(like3);
        voteJpaRepository.save(like4);

        ParagraphsOfChapter paragraphs = paragraphService.getParagraphs(author1.getId(), chapter.getId());

        // then
        assertNull(paragraphs.getMyParagraph());
        assertThat(paragraphs.getSelectedParagraphs()).isEqualTo(Collections.emptyList());
        assertThat(paragraphs.getBestParagraph().getContent()).isEqualTo(paragraph1.getContent());
    }

    @Test
    @DisplayName("투표 중인 한줄들 보기")
    void getInVotingParagraphs() {
        // given

        // when
        GetParagraphResponse paragraphs = paragraphService.getInVotingParagraphs(chapter.getId());

        // then
        assertThat(paragraphs.getParagraphInVotings().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("작성이 완료된 회차 보기")
    void getCompletedChParagraph() {
        // given
        List<Long> idList = Arrays.asList(paragraph1.getId(), paragraph2.getId());

        // when
        chapter.setStatusForTest(ChapterStatus.COMPLETED);
        paragraph1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
        ParagraphsOfChapter paragraphs = paragraphService.getParagraphs(author.getId(), chapter.getId());

        // then
        assertNull(paragraphs.getMyParagraph());
        assertNull(paragraphs.getBestParagraph());
        assertThat(paragraphs.getSelectedParagraphs().size()).isEqualTo(2);
        paragraphs.getSelectedParagraphs().forEach(p ->
                assertThat(p.getId()).isIn(idList));
    }
    
    @Test
    @DisplayName("한줄 쓰기 등록")
    void postParagraph() {
        // given
        String content = RandomStringUtils.random(200);
        PostParagraphReq req = PostParagraphReq.builder().content(content).build();

        // when
        ParagraphInVoting paragraphInVoting = assertDoesNotThrow(() -> paragraphService.postParagraph(author.getId(), chapter.getId(), req));
        List<Paragraph> paragraphs = paragraphRepository.findAllByChapter(chapter);

        // then
        assertThat(paragraphInVoting.getContent()).isEqualTo(content);
        assertThat(paragraphInVoting.getCreatedAt()).isNotNull();
        assertThat(paragraphInVoting.getUpdatedAt()).isNotNull();
        assertThat(paragraphInVoting.getVoteCnt()).isZero();
        assertThat(paragraphInVoting.getAuthorName()).isEqualTo(author.getNickname());
        assertThat(paragraphs.stream().anyMatch(p ->
                p.getContent().equals(req.getContent()))).isTrue();
    }

    @Test
    @DisplayName("300줄 이상 한줄 쓰기 등록")
    void postParagraphException1() {
        // given
        String longContent = RandomStringUtils.random(301);
        PostParagraphReq req = PostParagraphReq.builder().content(longContent).build();

        // when // then
        BaseException baseException = assertThrows(BaseException.class,
                () -> paragraphService.postParagraph(author.getId(), chapter.getId(), req));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.PARAGRAPH_LENGTH_OUT_OF_RANGE);

    }

    @Test
    @DisplayName("50줄 이하 한줄 쓰기 등록")
    void postParagraphException2() {
        // given
        String content = RandomStringUtils.random(49);
        PostParagraphReq req = PostParagraphReq.builder().content(content).build();

        // when // then
        BaseException baseException = assertThrows(BaseException.class,
                () -> paragraphService.postParagraph(author.getId(), chapter.getId(), req));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.PARAGRAPH_LENGTH_OUT_OF_RANGE);

    }

    @Test
    @DisplayName("메인 작가가 마음에 드는 한줄 선택하기")
    void selectParagraph1() {
        // given

        // when
        paragraphService.selectParagraph(author.getId(), novel.getId(), chapter.getId(), paragraph1.getId());
        paragraph1 = paragraphRepository.getParagraphById(this.paragraph1.getId()).get();
        paragraph2 = paragraphRepository.getParagraphById(this.paragraph2.getId()).get();

        // then
        assertThat(this.paragraph1.getParagraphStatus()).isEqualTo(ParagraphStatus.SELECTED);
        assertThat(paragraph2.getParagraphStatus()).isEqualTo(ParagraphStatus.UNSELECTED);
    }

    @Test
    @DisplayName("메인 작가가 아닌 사람이 마음에 드는 한줄 선택하기")
    void selectParagraph2() {
        // given
        Author newAuthor = AuthorMother.generateAuthor();
        setMember(newAuthor);

        // when
        authorRepository.save(newAuthor);
        BaseException baseException = assertThrows(BaseException.class,
                () -> paragraphService.selectParagraph(newAuthor.getId(), novel.getId(), chapter.getId(), paragraph1.getId()));
        paragraph1 = paragraphRepository.getParagraphById(this.paragraph1.getId()).get();
        paragraph2 = paragraphRepository.getParagraphById(this.paragraph2.getId()).get();

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED);
        assertThat(paragraph1.getParagraphStatus()).isEqualTo(ParagraphStatus.IN_VOTING);
        assertThat(paragraph2.getParagraphStatus()).isEqualTo(ParagraphStatus.IN_VOTING);
    }

    private void setMember(Author author) {
        Member member = MemberMother.generateMember();
        memberRepository.save(member);
        author.setMember(member);
    }
}