package us.usserver.domain.paragraph.service;

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
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.ParagraphLike;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.domain.member.MemberMother;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Rollback
@Transactional
@SpringBootTest
class ParagraphLikeServiceTest {
    @Autowired
    private ParagraphLikeService paragraphLikeService;

    @Autowired
    private ParagraphLikeRepository paragraphLikeRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Novel novel;
    private Author author;
    private Member member;
    private Chapter chapter;
    private Paragraph paragraph;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        paragraph = ParagraphMother.generateParagraph(author, chapter);

        novel.getChapters().add(chapter);
        chapter.getParagraphs().add(paragraph);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph);
    }

    @Test
    @DisplayName("한줄 좋아요")
    void setParagraphLike_0() {
        // given
        paragraph.setParagraphStatusForTest(ParagraphStatus.SELECTED);

        // when
        paragraphRepository.save(paragraph);
        paragraphLikeService.setParagraphLike(paragraph.getId(), author.getId());
        List<ParagraphLike> paragraphLikes = paragraphLikeRepository.findAllByParagraph(paragraph);

        // then
        assertThat(paragraphLikes).isNotEmpty();
    }

    @Test
    @DisplayName("한줄 좋아요 실패(투표중인 한줄)")
    void setParagraphLike_1() {
        // given
        paragraph.setParagraphStatusForTest(ParagraphStatus.IN_VOTING);

        // when
        paragraphRepository.save(paragraph);
        assertThrows(UnsupportedOperationException.class,
                () -> paragraphLikeService.setParagraphLike(paragraph.getId(), author.getId()));
        List<ParagraphLike> paragraphLikes = paragraphLikeRepository.findAllByParagraph(paragraph);

        // then
        assertThat(paragraphLikes).isEmpty();
    }

    @Test
    @DisplayName("한줄 좋아요 실패(선택 안된 한줄)")
    void setParagraphLike_2() {
        // given
        paragraph.setParagraphStatusForTest(ParagraphStatus.UNSELECTED);

        // when
        paragraphRepository.save(paragraph);
        assertThrows(UnsupportedOperationException.class,
                () -> paragraphLikeService.setParagraphLike(paragraph.getId(), author.getId()));
        List<ParagraphLike> paragraphLikes = paragraphLikeRepository.findAllByParagraph(paragraph);

        // then
        assertThat(paragraphLikes).isEmpty();
    }

    @Test
    @DisplayName("한줄 좋아요 중복 불가")
    void setParagraphLike_3() {
        // given
        paragraph.setParagraphStatusForTest(ParagraphStatus.SELECTED);

        // when
        paragraphRepository.save(paragraph);
        assertDoesNotThrow(
                () -> paragraphLikeService.setParagraphLike(paragraph.getId(), author.getId()));
        BaseException baseException = assertThrows(BaseException.class,
                () -> paragraphLikeService.setParagraphLike(paragraph.getId(), author.getId()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.LIKE_DUPLICATED);

    }

    @Test
    @DisplayName("한줄 좋아요 삭제")
    void deleteParagraphLike2() {
        // given
        ParagraphLike paragraphLike = ParagraphLike.builder().paragraph(paragraph).author(author).build();

        // when
        paragraphLikeRepository.save(paragraphLike);
        assertDoesNotThrow(
                () -> paragraphLikeService.deleteParagraphLike(paragraph.getId(), author.getId()));
        List<ParagraphLike> paragraphLikes = paragraphLikeRepository.findAllByAuthor(author);

        // then
        assertThat(paragraphLikes.size()).isZero();
    }
}