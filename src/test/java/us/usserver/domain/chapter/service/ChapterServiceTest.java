package us.usserver.domain.chapter.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.comment.CommentMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.chapter.dto.ChapterStatus;
import us.usserver.domain.chapter.dto.ChapterDetailInfo;
import us.usserver.domain.chapter.dto.ChapterInfo;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.comment.entity.Comment;
import us.usserver.domain.comment.entity.CommentLike;
import us.usserver.domain.comment.repository.CommentLikeRepository;
import us.usserver.domain.comment.repository.CommentRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class ChapterServiceTest {
    @Autowired
    private ChapterService chapterService;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CommentLikeRepository commentLikeRepository;

    private Novel novel;
    private Author author;
    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        novel = NovelMother.generateNovel(author);
        novelRepository.save(novel);
    }

    @Test
    @DisplayName("회차 생성")
    void createChapter1() {
        // given

        // when
        assertDoesNotThrow(
                () -> chapterService.createChapter(novel.getId(), author.getId()));
        List<Chapter> chapters = chapterRepository.findAllByNovel(novel);

        // then
        assertThat(chapters.size()).isNotZero();
    }

    @Test
    @DisplayName("권한 없는 유저의 회차 생성")
    void createChapter2() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        // when
        authorRepository.save(newAuthor);

        // then
        BaseException baseException = assertThrows(BaseException.class,
                () -> chapterService.createChapter(novel.getId(), newAuthor.getId()));
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED);
    }

    @Test
    @DisplayName("소설 회차 정보 조회")
    void getChaptersOfNovel() {
        // given
        Chapter chapter1 = ChapterMother.generateChapter(novel);
        Chapter chapter2 = ChapterMother.generateChapter(novel);
        Chapter chapter3 = ChapterMother.generateChapter(novel);
        Chapter chapter4 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(0);
        chapter2.setPartForTest(1);
        chapter3.setPartForTest(2);
        chapter4.setPartForTest(3);

        // when
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);
        novel.getChapters().add(chapter4);
        novelRepository.save(novel);
        chapterRepository.save(chapter3);
        chapterRepository.save(chapter4);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);

        List<ChapterInfo> chapterInfos = chapterService.getChaptersOfNovel(novel);

        // then
        assertThat(chapterInfos.size()).isEqualTo(4);
        assertThat(chapterInfos.get(0).getId()).isEqualTo(chapter1.getId());
        assertThat(chapterInfos.get(1).getId()).isEqualTo(chapter2.getId());
        assertThat(chapterInfos.get(2).getId()).isEqualTo(chapter3.getId());
        assertThat(chapterInfos.get(3).getId()).isEqualTo(chapter4.getId());
    }

    @Test
    @DisplayName("빈 소설의 회차 조회 테스트")
    void getChaptersOfNotExistNovel() {
        // given

        // when
        List<ChapterInfo> chapterInfos = chapterService.getChaptersOfNovel(novel);

        // then
        assertThat(chapterInfos).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("회차 상세 정보 조회")
    void getChapterDetailInfo() {
        // given
        Chapter chapter1 = ChapterMother.generateChapter(novel);
        Chapter chapter2 = ChapterMother.generateChapter(novel);
        Chapter chapter3 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter1.setStatusForTest(ChapterStatus.COMPLETED);
        chapter2.setPartForTest(2);
        chapter2.setStatusForTest(ChapterStatus.COMPLETED);
        chapter3.setPartForTest(3);

        // when
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);
        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        chapterRepository.save(chapter3);
        ChapterDetailInfo chapterDetailInfo = chapterService.getChapterDetailInfo(novel.getId(), author.getId(), chapter2.getId());

        // then
        assertThat(chapterDetailInfo.part()).isEqualTo(chapter2.getPart());
        assertThat(chapterDetailInfo.title()).isEqualTo(chapter2.getTitle());
        assertThat(chapterDetailInfo.myParagraph()).isNull();
        assertThat(chapterDetailInfo.bestParagraph()).isNull();
        assertThat(chapterDetailInfo.selectedParagraphs()).isEqualTo(Collections.emptyList());
        assertThat(chapterDetailInfo.prevPart()).isEqualTo(chapter1.getPart());
        assertThat(chapterDetailInfo.nextPart()).isEqualTo(chapter3.getPart());
    }

    @Test
    @DisplayName("첫 회차, 마지막 회차 정보 조회")
    void getChapterDetailInfo2() {
        // given
        Chapter chapter1 = ChapterMother.generateChapter(novel);
        Chapter chapter2 = ChapterMother.generateChapter(novel);
        Chapter chapter3 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter1.setStatusForTest(ChapterStatus.COMPLETED);
        chapter2.setPartForTest(2);
        chapter2.setStatusForTest(ChapterStatus.COMPLETED);
        chapter3.setPartForTest(3);

        // when
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);
        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        chapterRepository.save(chapter3);
        ChapterDetailInfo chapterDetailInfo1 = chapterService.getChapterDetailInfo(novel.getId(), author.getId(), chapter1.getId());
        ChapterDetailInfo chapterDetailInfo3 = chapterService.getChapterDetailInfo(novel.getId(), author.getId(), chapter3.getId());

        // then
        assertThat(chapterDetailInfo1.part()).isEqualTo(chapter1.getPart());
        assertThat(chapterDetailInfo1.title()).isEqualTo(chapter1.getTitle());
        assertThat(chapterDetailInfo1.myParagraph()).isNull();
        assertThat(chapterDetailInfo1.bestParagraph()).isNull();
        assertThat(chapterDetailInfo1.selectedParagraphs()).isEqualTo(Collections.emptyList());
        assertThat(chapterDetailInfo1.prevPart()).isNull();
        assertThat(chapterDetailInfo1.nextPart()).isEqualTo(chapter2.getPart());

        assertThat(chapterDetailInfo3.part()).isEqualTo(chapter3.getPart());
        assertThat(chapterDetailInfo3.title()).isEqualTo(chapter3.getTitle());
        assertThat(chapterDetailInfo3.myParagraph()).isNull();
        assertThat(chapterDetailInfo3.bestParagraph()).isNull();
        assertThat(chapterDetailInfo3.selectedParagraphs()).isEqualTo(Collections.emptyList());
        assertThat(chapterDetailInfo3.prevPart()).isEqualTo(chapter2.getPart());
        assertThat(chapterDetailInfo3.nextPart()).isNull();
    }

    @Test
    @DisplayName("댓글 갯수, 베스트 댓글 정보 확인 테스트")
    void getChapterDetailInfo3() {
        // given
        Member member1 = MemberMother.generateMember();
        Author author1 = AuthorMother.generateAuthorWithMember(member1);
        member1.setAuthor(author1);
        memberRepository.save(member1);

        Member member2 = MemberMother.generateMember();
        Author author2 = AuthorMother.generateAuthorWithMember(member2);
        member2.setAuthor(author2);
        memberRepository.save(member2);

        Chapter chapter1 = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter1);
        chapterRepository.save(chapter1);

        Comment comment1 = CommentMother.generateComment(author, novel, chapter1);
        Comment comment2 = CommentMother.generateComment(author, novel, chapter1);
        Comment comment3 = CommentMother.generateComment(author, novel, chapter1);
        Comment comment4 = CommentMother.generateComment(author, novel, null);
        CommentLike commentLike1 = CommentLike.builder().author(author1).comment(comment1).build();
        CommentLike commentLike2 = CommentLike.builder().author(author2).comment(comment1).build();
        CommentLike commentLike3 = CommentLike.builder().author(author1).comment(comment2).build();
        CommentLike commentLike4 = CommentLike.builder().author(author2).comment(comment2).build();
        comment1.getCommentLikes().add(commentLike1);
        comment1.getCommentLikes().add(commentLike2);
        comment2.getCommentLikes().add(commentLike3);
        comment2.getCommentLikes().add(commentLike4);
        chapter1.getComments().add(comment1);
        chapter1.getComments().add(comment2);
        chapter1.getComments().add(comment3);
        novel.getComments().add(comment4);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        commentRepository.save(comment4);
        commentLikeRepository.save(commentLike1);
        commentLikeRepository.save(commentLike2);
        commentLikeRepository.save(commentLike3);
        commentLikeRepository.save(commentLike4);

        // when
        ChapterDetailInfo chapterDetailInfo = chapterService.getChapterDetailInfo(novel.getId(), author.getId(), chapter1.getId());

        // then
        assertThat(chapterDetailInfo.commentCnt()).isEqualTo(3);
        assertThat(chapterDetailInfo.bestComments().size()).isEqualTo(3);
        assertThat(chapterDetailInfo.bestComments().get(0).getId()).isEqualTo(comment1.getId());
    }

    @Test
    @DisplayName("글자 크기, 문단 간격 정보 확인 테스트")
    void getChapterDetailInfo4() {
        // given
        Chapter chapter1 = ChapterMother.generateChapter(novel);
        Chapter chapter2 = ChapterMother.generateChapter(novel);
        Chapter chapter3 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter1.setStatusForTest(ChapterStatus.COMPLETED);
        chapter2.setPartForTest(2);
        chapter2.setStatusForTest(ChapterStatus.COMPLETED);
        chapter3.setPartForTest(3);

        // when
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);
        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        chapterRepository.save(chapter3);
        ChapterDetailInfo chapterDetailInfo = chapterService.getChapterDetailInfo(novel.getId(), author.getId(), chapter2.getId());

        // then
        assertThat(chapterDetailInfo.fontSize()).isEqualTo(15);
        assertThat(chapterDetailInfo.paragraphSpace()).isEqualTo(16);
    }
}