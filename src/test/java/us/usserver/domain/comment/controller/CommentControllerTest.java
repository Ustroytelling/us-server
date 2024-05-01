package us.usserver.domain.comment.controller;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import us.usserver.domain.comment.repository.CommentRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class CommentControllerTest {
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
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;
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

        accessToken = tokenProvider.issueAccessToken(member.getId());
        refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(1));

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
    }

    @Test
    @DisplayName("소설 댓글 조회 API TEST")
    void getCommentsOfNovel1() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + novel.getId())
                .param("page", "0")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("소설 댓글 조회(요청 파라미터 생략) API TEST")
    void getCommentsOfNovel2() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + novel.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("소설 댓글 조회 실패(noveId Long 범위 밖) API TEST")
    void getCommentsOfNovel3() throws Exception {
        // given
        String overMaxLong = "9223372036854775808";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + overMaxLong)
                .param("page", "0")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));


        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("소설 댓글 조회 실패(page 음수) API TEST")
    void getCommentsOfNovel4() throws Exception {
        // given
        String minusPage = "-1";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + novel.getId())
                .param("page", minusPage)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
        Assertions.assertThat(Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getMessage()).isEqualTo(ExceptionMessage.PAGE_INDEX_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("소설 댓글 조회 실패(page 문자) API TEST")
    void getCommentsOfNovel5() throws Exception {
        // given
        String strParam = "exit()";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + novel.getId())
                .param("page", strParam)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("소설 댓글 조회 실패(page int 범위 밖) API TEST")
    void getCommentsOfNovel6() throws Exception {
        // given
        String overMaxInt = "2147483649";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/novel/" + novel.getId())
                .param("page", overMaxInt)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회차 댓글 조회 API TEST")
    void getCommentsOfChapter1() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/chapter/" + chapter.getId())
                .param("page", "0")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("회차 댓글 조회 실패(회차가 null)API TEST")
    void getCommentsOfChapter2() throws Exception {
        // given
        Long notExistChapterId = null;

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/chapter/" + notExistChapterId)
                .param("page", "0")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("소설 댓글 작성 API TEST")
    void postCommentOnNovel1() throws Exception {
        // given
        String requestJson = "{\"content\":\"comment sample\"}";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/comment/novel/" + novel.getId())
                .param("page", "0")
                .content(requestJson)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(resultString).contains(author.getNickname());
        Assertions.assertThat(resultString).contains(novel.getTitle());
    }

    @Test
    @DisplayName("회차 댓글 작성 API TEST")
    void postCommentOnNovel2() throws Exception {
        // given
        String requestJson = "{\"content\":\"comment sample\"}";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/comment/chapter/" + chapter.getId())
                .param("page", "0")
                .content(requestJson)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(resultString).contains(author.getNickname());
        Assertions.assertThat(resultString).contains(chapter.getTitle());
    }

    @Test
    @DisplayName("회차 댓글 작성 실패(너무 긴 댓글) API TEST")
    void postCommentOnChapter2() throws Exception {
        // given
        String longComment = "가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하가나다라마바사아자차카타파하";
        String requestJson = "{\"content\":\"" + longComment + "\"}";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/comment/chapter/" + chapter.getId())
                .param("page", "0")
                .content(requestJson)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("본인 작성 댓글 조회 API TEST")
    void getCommentsOfAuthor() throws Exception {
        // given
        Comment comment = CommentMother.generateComment(author, novel, chapter);
        chapter.addComment(comment);

        // when
        commentRepository.save(comment);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/comment/author")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
        String resultString = resultActions.andReturn().getResponse().getContentAsString();
        Assertions.assertThat(resultString).contains(comment.getContent());
        Assertions.assertThat(resultString).contains(author.getNickname());
    }

    @Test
    @DisplayName("작성 댓글 삭제 API TEST")
    void deleteComment1() throws Exception {
        // given
        Comment comment = CommentMother.generateComment(author, novel, chapter);
        chapter.addComment(comment);

        // when
        comment = commentRepository.save(comment);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/comment/" + comment.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        Optional<Comment> commentById = commentRepository.getCommentById(comment.getId());

        // then
        resultActions.andExpect(status().isOk());
        Assertions.assertThat(commentById.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("작성 댓글 삭제 실패(본인 댓글 아님) API TEST")
    void deleteComment2() throws Exception {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        Comment comment = CommentMother.generateComment(newAuthor, novel, chapter);
        chapter.addComment(comment);

        // when
        comment = commentRepository.save(comment);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/comment/" + comment.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        Optional<Comment> commentById = commentRepository.getCommentById(comment.getId());

        // then
        resultActions.andExpect(status().isForbidden());
        Assertions.assertThat(Objects.requireNonNull(resultActions.andReturn().getResolvedException()).getMessage())
                .isEqualTo(ExceptionMessage.AUTHOR_NOT_AUTHORIZED);
    }
}
