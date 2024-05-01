package us.usserver.domain.paragraph.controller;

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
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ParagraphControllerTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;

    private Member member;
    private Author author;
    private Novel novel;
    private Chapter chapter;
    private Paragraph paragraph;

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
        chapter.setPartForTest(1);
        novel.getChapters().add(chapter);

        paragraph = ParagraphMother.generateParagraph(author, chapter);
        paragraph.setSequenceForTest(1);
        chapter.getParagraphs().add(paragraph);

        authorRepository.save(author);
        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph);

    }

    @Test
    @DisplayName("투표중인 한줄 조회 API TEST")
    void getParagraphsInVoting_1() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/paragraph/" + chapter.getId() + "/voting")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("빈 챕터의 투표중인 한줄 조회 API TEST")
    void getParagraphsInVoting_2() throws Exception {
        // given
        Chapter newChapter = ChapterMother.generateChapter(novel);
        novel.addChapter(newChapter);

        // when
        chapterRepository.save(newChapter);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/paragraph/" + newChapter.getId() + "/voting")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("[]");
    }

    @Test
    @DisplayName("한줄 작성 API TEST")
    void postParagraph_1() throws Exception {
        // given
        String requestJson = "{\"content\":\"SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT SOME CONTENT\"}";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/paragraph/" + chapter.getId())
                .header("Authorization", "Bearer " + accessToken)
                .content(requestJson)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isCreated());
        assertThat(resultString).contains("SOME CONTENT");
        assertThat(resultString).contains(author.getNickname());
    }

    @Test
    @DisplayName("한줄 작성 실패(req Body 없음) API TEST")
    void postParagraph_2() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/paragraph/" + chapter.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("한줄 선택 API TEST")
    void selectParagraph_1() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/paragraph/" + novel.getId() + "/" + chapter.getId() + "/" + paragraph.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());
    }

    @Test
    @DisplayName("한줄 신고 API TEST(기능 미완성)")
    void selectParagraph_2() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/paragraph/call/" + paragraph.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());
    }
}