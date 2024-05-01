package us.usserver.domain.comment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import java.time.Duration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class CommentLikeControllerTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private RedisUtils redisUtils;

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

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;
    private Novel novel;
    private Member member;
    private Author author;
    private Chapter chapter;
    private Comment comment;


    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        accessToken = tokenProvider.issueAccessToken(member.getId());
        refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(1));

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter);
        comment = CommentMother.generateComment(author, novel, chapter);
        chapter.addComment(comment);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
        commentRepository.save(comment);
    }

    @Test
    void setLike_좋아요_API_TEST() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/like/comment/" + comment.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void setLike_좋아요_실패_존재하지않는_댓글_API_TEST() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/like/comment/" + 99999L)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    void deleteLike_좋아요_취소_API_TEST() throws Exception {
        // given
        CommentLike commentLike = CommentLike.builder().comment(comment).author(author).build();
        comment.getCommentLikes().add(commentLike);

        // when
        commentLikeRepository.save(commentLike);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/like/comment/" + comment.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteLike_권한없는_유저의_좋아요_취소_실패_API_TEST() throws Exception {
        // given
        CommentLike commentLike = CommentLike.builder().comment(comment).author(author).build();
        comment.getCommentLikes().add(commentLike);

        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);
        String newAccessToken = tokenProvider.issueAccessToken(newMember.getId());
        String newRefreshToken = tokenProvider.issueRefreshToken(newMember.getId());
        redisUtils.setDateWithExpiration(newRefreshToken, newMember.getId(), Duration.ofDays(1));

        // when
        commentLikeRepository.save(commentLike);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/like/comment/" + comment.getId())
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Authorization-Refresh", "Bearer " + newRefreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void deleteLike_존재하지않는_댓글의_좋아요_취소_실패_API_TEST() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/like/comment/" + 999999L)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isNotFound());
    }
}