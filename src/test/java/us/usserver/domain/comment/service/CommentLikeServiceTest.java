package us.usserver.domain.comment.service;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class CommentLikeServiceTest {
    @Autowired
    private CommentLikeService commentLikeService;

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

    private Member member;
    private Author author;
    private Novel novel;
    private Chapter chapter;
    private Comment comment;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter);
        comment = CommentMother.generateComment(author, novel, chapter);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
        commentRepository.save(comment);
    }

    @Test
    @DisplayName("좋아요 테스트")
    void postLike1() {
        // given

        // when
        commentLikeService.postLike(comment.getId(), member.getId());
        Optional<CommentLike> byComment = commentLikeRepository.findByComment(comment);

        // then
        assertThat(byComment.isPresent()).isTrue();
    }

    @Test
    @DisplayName("좋아요 테스트 실패(존재하지 않는 댓글)")
    void postLike2() {
        // given

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentLikeService.postLike(99999L, member.getId()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("좋아요 취소 테스트")
    void deleteLike1() {
        // given
        CommentLike commentLike = CommentLike.builder().comment(comment).author(author).build();
        comment.addCommentLike(commentLike);

        // when
        commentLikeRepository.save(commentLike);
        commentLikeService.deleteLike(comment.getId(), member.getId());
        Optional<CommentLike> byComment = commentLikeRepository.findByComment(comment);

        // then
        assertThat(byComment.isPresent()).isFalse();
    }

    @Test
    @DisplayName("좋아요 취소 실패(본인 좋아요가 아님)")
    void deleteLike2() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);
        CommentLike commentLike = CommentLike.builder().comment(comment).author(newAuthor).build();
        comment.addCommentLike(commentLike);

        // when
        commentLikeRepository.save(commentLike);
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentLikeService.deleteLike(comment.getId(), member.getId()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_AUTHORIZED);
    }

    @Test
    @DisplayName("좋아요 취소 실패(존재하지 않는 댓글)")
    void deleteLike3() {
        // given

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentLikeService.deleteLike(99999L, member.getId()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.COMMENT_NOT_FOUND);
    }
}