package us.usserver.domain.comment.service;

import org.apache.commons.lang3.RandomStringUtils;
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
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.comment.dto.CommentContent;
import us.usserver.domain.comment.dto.CommentInfo;
import us.usserver.domain.comment.dto.GetCommentRes;
import us.usserver.domain.comment.entity.Comment;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Transactional
@SpringBootTest
class CommentServiceTest {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    private Novel novel;
    private Member member;
    private Author author;
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
    @DisplayName("소설 댓글 불러오기 - 0개")
    void getCommentsOfNovel() {
        // given

        // when
        GetCommentRes commentsOfNovel = commentService.getCommentsOfNovel(novel.getId(), 0);

        // then
        assertThat(commentsOfNovel.commentInfos()).isEqualTo(Collections.emptyList());
    }

    @Test
    @DisplayName("소설 댓글 불러오기 - n개")
    void getCommentsOfNovel1() {
        // given
        Comment comment1 = CommentMother.generateComment(author, novel, chapter);
        Comment comment2 = CommentMother.generateComment(author, novel, chapter);
        Comment comment3 = CommentMother.generateComment(author, novel, null);

        // when
        novel.getComments().add(comment1);
        novel.getComments().add(comment2);
        novel.getComments().add(comment3);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        GetCommentRes commentsOfNovel = commentService.getCommentsOfNovel(novel.getId(), 0);

        // then
        assertThat(commentsOfNovel.commentInfos().size()).isEqualTo(3);
        assertThat(commentsOfNovel.commentInfos().get(0).getLocation()).isEqualTo(novel.getTitle());
        assertThat(commentsOfNovel.commentInfos().get(1).getLocation()).isEqualTo(chapter.getTitle());
        assertThat(commentsOfNovel.commentInfos().get(2).getLocation()).isEqualTo(chapter.getTitle());
    }

    @Test
    @DisplayName("소설 댓글 불러오기 - 삭제된 소설")
    void getCommentsOfNovel2() {
        // given
        Novel newNovel = NovelMother.generateNovel(author);
        Comment newComment = CommentMother.generateComment(author, newNovel, null);
        newNovel.getComments().add(newComment);

        // when
        novelRepository.save(newNovel);
        commentRepository.save(newComment);
        GetCommentRes before = commentService.getCommentsOfNovel(newNovel.getId(), 0);
        novelRepository.delete(newNovel);
        List<Comment> afterComments = commentRepository.findAllByAuthor(author);
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.getCommentsOfNovel(newNovel.getId(), 0));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.NOVEL_NOT_FOUND);
        assertThat(before.commentInfos().get(0).getLocation()).isEqualTo(newNovel.getTitle());
        for (Comment comment : afterComments) {
            assertThat(comment.getNovel().getTitle()).isNotEqualTo(newNovel.getTitle());
        }
    }

    @Test
    @DisplayName("회차 댓글 불러오기 - 0개")
    void getCommentsOfChapter() {
        // given

        // when
        GetCommentRes commentsOfChapter = commentService.getCommentsOfChapter(chapter.getId(), 0);

        // then
        assertThat(commentsOfChapter.commentInfos().size()).isZero();
    }

    @Test
    @DisplayName("회차 댓글 불러오기 - n개")
    void getCommentsOfChapter1() {
        // given
        Comment comment1 = CommentMother.generateComment(author, novel, chapter);
        Comment comment2 = CommentMother.generateComment(author, novel, chapter);
        Comment comment3 = CommentMother.generateComment(author, novel, chapter);

        // when
        chapter.getComments().add(comment1);
        chapter.getComments().add(comment2);
        chapter.getComments().add(comment3);
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);
        GetCommentRes commentsOfChapter = commentService.getCommentsOfChapter(chapter.getId(), 0);

        // then
        assertThat(commentsOfChapter.commentInfos().size()).isEqualTo(3);
        assertThat(commentsOfChapter.commentInfos().get(0).getLocation()).isEqualTo(chapter.getTitle());
        assertThat(commentsOfChapter.commentInfos().get(1).getLocation()).isEqualTo(chapter.getTitle());
        assertThat(commentsOfChapter.commentInfos().get(2).getLocation()).isEqualTo(chapter.getTitle());
    }

    @Test
    @DisplayName("회차 댓글 불러오기 - 삭제된 회차(챕터)")
    void getCommentsOfChapter2() {
        // given
        Chapter newChapter = ChapterMother.generateChapter(novel);
        Comment newComment = CommentMother.generateComment(author, novel, newChapter);

        // when
        novel.getChapters().add(newChapter);
        newChapter.getComments().add(newComment);
        chapterRepository.save(newChapter);
        GetCommentRes before = commentService.getCommentsOfChapter(newChapter.getId(), 0);
        chapterRepository.delete(newChapter);
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.getCommentsOfChapter(newChapter.getId(), 0));
        List<Comment> afterComments = commentRepository.findAllByAuthor(author);

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.CHAPTER_NOT_FOUND);
        assertThat(before.commentInfos().size()).isEqualTo(1);
        assertThat(before.commentInfos().get(0).getLocation()).isEqualTo(newChapter.getTitle());
        for (Comment comment : afterComments) {
            assertThat(comment.getChapter().getTitle()).isNotEqualTo(newChapter.getTitle());
        }
    }

    @Test
    @DisplayName("소설 댓글 작성하기")
    void writeCommentOnNovel() {
        // given
        CommentContent commentContent = CommentContent.builder().content("SOME COMMENT CONTENT").build();

        // when
        CommentInfo commentInfo = commentService.writeCommentOnNovel(novel.getId(), author.getId(), commentContent);

        // then
        assertThat(commentInfo.getAuthorName()).isEqualTo(author.getNickname());
        assertThat(commentInfo.getLocation()).isEqualTo(novel.getTitle());
        assertThat(commentInfo.getContent()).isEqualTo(commentContent.content());
        assertThat(commentInfo.getLikeCnt()).isZero();
    }

    @Test
    @DisplayName("소설 댓글 작성하기 - 존재하지 않는 작가")
    void writeCommentOnNovel1() {
        // given
        CommentContent commentContent = CommentContent.builder().content("SOME COMMENT CONTENT").build();
        Author newAuthor = AuthorMother.generateAuthor();

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnNovel(novel.getId(), newAuthor.getId(), commentContent));
        List<Comment> comments = commentRepository.findAllByNovel(novel);

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);
        assertThat(comments.size()).isZero();
    }

    @Test
    @DisplayName("소설 댓글 작성 오류 - 0자 or 300자 초과")
    void writeCommentOnNovel2() {
        // given
        String content1 = RandomStringUtils.random(301);
        String content2 = "";
        CommentContent commentContent1 = CommentContent.builder().content(content1).build();
        CommentContent commentContent2 = CommentContent.builder().content(content2).build();

        // when
        BaseException baseException1 = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnNovel(novel.getId(), author.getId(), commentContent1));
        BaseException baseException2 = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnNovel(novel.getId(), author.getId(), commentContent2));

        // then
        assertThat(baseException1.getMessage()).isEqualTo(ExceptionMessage.COMMENT_LENGTH_OUT_OF_RANGE);
        assertThat(baseException2.getMessage()).isEqualTo(ExceptionMessage.COMMENT_LENGTH_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("회차 댓글 작성하기")
    void writeCommentOnChapter() {
        // given
        CommentContent commentContent = CommentContent.builder().content("SOME COMMENT CONTENT").build();

        // when
        CommentInfo commentInfo = commentService.writeCommentOnChapter(chapter.getId(), author.getId(), commentContent);

        // then
        assertThat(commentInfo.getAuthorName()).isEqualTo(author.getNickname());
        assertThat(commentInfo.getLocation()).isEqualTo(chapter.getTitle());
        assertThat(commentInfo.getContent()).isEqualTo(commentContent.content());
        assertThat(commentInfo.getLikeCnt()).isZero();
    }

    @Test
    @DisplayName("회차 댓글 작성하기 - 존재하지 않는 작가")
    void writeCommentOnChapter1() {
        // given
        CommentContent commentContent = CommentContent.builder().content("SOME COMMENT CONTENT").build();
        Author newAuthor = AuthorMother.generateAuthor();

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnChapter(chapter.getId(), newAuthor.getId(), commentContent));
        List<Comment> comments = commentRepository.findAllByChapter(chapter);

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);
        assertThat(comments.size()).isZero();
    }

    @Test
    @DisplayName("회차 댓글 작성하기 - 0자 or 300자 초과")
    void writeCommentOnChapter2() {
        // given
        String content1 = RandomStringUtils.random(301);
        String content2 = "";
        CommentContent commentContent1 = CommentContent.builder().content(content1).build();
        CommentContent commentContent2 = CommentContent.builder().content(content2).build();

        // when then
        BaseException baseException1 = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnChapter(chapter.getId(), author.getId(), commentContent1));
        BaseException baseException2 = assertThrows(BaseException.class,
                () -> commentService.writeCommentOnChapter(chapter.getId(), author.getId(), commentContent2));

        // then
        assertThat(baseException1.getMessage()).isEqualTo(ExceptionMessage.COMMENT_LENGTH_OUT_OF_RANGE);
        assertThat(baseException2.getMessage()).isEqualTo(ExceptionMessage.COMMENT_LENGTH_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("내가 쓴 댓글 로드 - 0개")
    void getCommentsByAuthor() {
        // given

        // when
        GetCommentRes commentsByAuthor = commentService.getCommentsByAuthor(author.getId());

        // then
        assertThat(commentsByAuthor.commentInfos().size()).isZero();
    }

    @Test
    @DisplayName("소설, 회차 모두에서 내가 쓴 댓글 로드 - n개")
    void getCommentsByAuthor1() {
        // given
        Comment novelComment = CommentMother.generateComment(author, novel, null);
        Comment chapterComment = CommentMother.generateComment(author, novel, chapter);

        // when
        novel.getComments().add(novelComment);
        chapter.getComments().add(chapterComment);
        commentRepository.save(novelComment);
        commentRepository.save(chapterComment);

        GetCommentRes commentsByAuthor = commentService.getCommentsByAuthor(author.getId());

        // then
        assertThat(commentsByAuthor.commentInfos().size()).isEqualTo(2);
        assertThat(commentsByAuthor.commentInfos().get(0).getLocation()).isEqualTo(novel.getTitle());
        assertThat(commentsByAuthor.commentInfos().get(1).getLocation()).isEqualTo(chapter.getTitle());
    }

    @Test
    @DisplayName("삭제된 소설, 회차에서 삭제된 댓글 확인")
    void getCommentsByAuthor2() {
        // given
        Comment novelComment = CommentMother.generateComment(author, novel, null);
        Comment chapterComment = CommentMother.generateComment(author, novel, chapter);

        // when
        novel.getComments().add(novelComment);
        chapter.getComments().add(chapterComment);
        commentRepository.save(novelComment);
        commentRepository.save(chapterComment);
        GetCommentRes before = commentService.getCommentsByAuthor(author.getId());
        commentRepository.delete(novelComment);
        commentRepository.delete(chapterComment);
        GetCommentRes after = commentService.getCommentsByAuthor(author.getId());

        // then
        assertThat(before.commentInfos().size()).isEqualTo(2);
        assertThat(before.commentInfos().get(0).getLocation()).isEqualTo(novel.getTitle());
        assertThat(before.commentInfos().get(1).getLocation()).isEqualTo(chapter.getTitle());
        assertThat(after.commentInfos().size()).isZero();
    }

    @Test
    @DisplayName("소설 댓글 삭제")
    void deleteComment() {
        // given
        Comment comment = CommentMother.generateComment(author, novel, null);

        // when
        novel.getComments().add(comment);
        commentRepository.save(comment);
        commentService.deleteComment(comment.getId(), author.getId());
        Optional<Comment> commentById = commentRepository.getCommentById(comment.getId());

        // then
        assertTrue(commentById.isEmpty());
    }

    @Test
    @DisplayName("회차 댓글 삭제")
    void deleteComment1() {
        // given
        Comment comment = CommentMother.generateComment(author, novel, chapter);

        // when
        chapter.getComments().add(comment);
        commentRepository.save(comment);
        commentService.deleteComment(comment.getId(), author.getId());
        Optional<Comment> commentById = commentRepository.getCommentById(comment.getId());

        // then
        assertTrue(commentById.isEmpty());
    }

    @Test
    @DisplayName("댓글 삭제 - 존재하지 않는 댓글")
    void deleteComment2() {
        // given
        Comment comment = CommentMother.generateComment(author, novel, chapter);

        // when
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.deleteComment(comment.getId(), author.getId()));

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.COMMENT_NOT_FOUND);
    }

    @Test
    @DisplayName("댓글 삭제 - 작성자가 본인이 아닌 댓글")
    void deleteComment3() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);
        Comment comment = CommentMother.generateComment(author, novel, chapter);

        // when
        chapter.getComments().add(comment);
        commentRepository.save(comment);
        authorRepository.save(newAuthor);
        BaseException baseException = assertThrows(BaseException.class,
                () -> commentService.deleteComment(comment.getId(), newAuthor.getId()));
        Optional<Comment> commentById = commentRepository.getCommentById(comment.getId());

        // then
        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_AUTHORIZED);
        assertFalse(commentById.isEmpty());
    }

    @Test
    @DisplayName("소설 댓글 불러오기 페이징 TEST")
    void getNovelCommentsPaging1() {
        // given
        for (int i = 0; i < 15; i++) {
            Comment newComment = CommentMother.generateComment(author, novel, null);
            commentRepository.save(newComment);
        }

        // when
        GetCommentRes commentsOfNovel0 = commentService.getCommentsOfNovel(novel.getId(), 0);
        GetCommentRes commentsOfNovel1 = commentService.getCommentsOfNovel(novel.getId(), 1);

        // then
        assertThat(commentsOfNovel0.commentInfos().size()).isEqualTo(10);
        assertThat(commentsOfNovel1.commentInfos().size()).isEqualTo(5);
        commentsOfNovel0.commentInfos().forEach(commentInfo -> assertThat(commentInfo.getLocation()).isEqualTo(novel.getTitle()));
        commentsOfNovel1.commentInfos().forEach(commentInfo -> assertThat(commentInfo.getLocation()).isEqualTo(novel.getTitle()));
    }


    @Test
    @DisplayName("소설 댓글 불러오기 페이징 실패(page 음수 값)")
    void getNovelCommentsPaging2() {
        // given

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.getCommentsOfNovel(novel.getId(), -1));

        // then
        assertThat(illegalArgumentException.getMessage()).isEqualTo(ExceptionMessage.PAGE_INDEX_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("회차 댓글 불러오기 페이징 TEST")
    void getChapterCommentsPaging1() {
        // given
        for (int i = 0; i < 15; i++) {
            Comment newComment = CommentMother.generateComment(author, novel, chapter);
            commentRepository.save(newComment);
        }

        // when
        GetCommentRes commentsOfNovel0 = commentService.getCommentsOfChapter(chapter.getId(), 0);
        GetCommentRes commentsOfNovel1 = commentService.getCommentsOfChapter(chapter.getId(), 1);

        // then
        assertThat(commentsOfNovel0.commentInfos().size()).isEqualTo(10);
        assertThat(commentsOfNovel1.commentInfos().size()).isEqualTo(5);
        commentsOfNovel0.commentInfos().forEach(commentInfo -> assertThat(commentInfo.getLocation()).isEqualTo(chapter.getTitle()));
        commentsOfNovel1.commentInfos().forEach(commentInfo -> assertThat(commentInfo.getLocation()).isEqualTo(chapter.getTitle()));
    }

    @Test
    @DisplayName("회차 댓글 불러오기 페이징 실패(page 음수 값)")
    void getChapterCommentsPaging2() {
        // given

        // when
        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class,
                () -> commentService.getCommentsOfChapter(novel.getId(), -1));

        // then
        assertThat(illegalArgumentException.getMessage()).isEqualTo(ExceptionMessage.PAGE_INDEX_OUT_OF_RANGE);
    }
}