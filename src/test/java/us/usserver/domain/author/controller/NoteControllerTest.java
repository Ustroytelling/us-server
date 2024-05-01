package us.usserver.domain.author.controller;

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
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.ParagraphLike;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.repository.VoteRepository;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class NoteControllerTest {
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
    private ParagraphRepository paragraphRepository;
    @Autowired
    private ParagraphLikeRepository paragraphLikeRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private MockMvc mockMvc;

    String accessToken;
    String refreshToken;
    Author author;
    Member member;
    Novel novel;
    Chapter chapter;
    Paragraph paragraph1, paragraph2, paragraph3;
    Authority authority;

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
        paragraph1 = ParagraphMother.generateParagraphWithSeed(author, chapter, 0);
        paragraph2 = ParagraphMother.generateParagraphWithSeed(author, chapter, 1);
        paragraph3 = ParagraphMother.generateParagraphWithSeed(author, chapter, 2);
        paragraph1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        authority = Authority.builder().author(author).novel(novel).build();

        novel.getChapters().add(chapter);
        chapter.getParagraphs().add(paragraph1);
        chapter.getParagraphs().add(paragraph2);
        chapter.getParagraphs().add(paragraph3);

        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
        paragraphRepository.save(paragraph3);
        authorityRepository.save(authority);
    }

    @Test
    @DisplayName("내가 쓴 한줄 불러오기 API TEST")
    void wroteParagraphs_1() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/notebook/wrote")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertTrue(resultString.contains(paragraph1.getContent()));
        assertTrue(resultString.contains(paragraph2.getContent()));
        assertTrue(resultString.contains(paragraph3.getContent()));
    }

    @Test
    @DisplayName("내가 투표한 한 한줄 불러오기 API TEST")
    void votedParagraphs_1() throws Exception {
        // given
        Vote vote = Vote.builder().author(author).paragraph(paragraph2).build();

        // when
        voteRepository.save(vote);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/notebook/voted")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertTrue(resultString.contains(paragraph2.getContent()));
        assertThat(resultString).doesNotContain(paragraph1.getContent());
        assertThat(resultString).doesNotContain(paragraph3.getContent());
    }

    @Test
    @DisplayName("내가 좋아요 한 한줄 불러오기 API TEST")
    void likedParagraphs_1() throws Exception {
        // given
        ParagraphLike paragraphLike = ParagraphLike.builder()
                .author(author).paragraph(paragraph2).build();

        // when
        paragraphLikeRepository.save(paragraphLike);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/notebook/liked")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertTrue(resultString.contains(paragraph2.getContent()));
        assertThat(resultString).doesNotContain(paragraph1.getContent());
        assertThat(resultString).doesNotContain(paragraph3.getContent());
    }
}