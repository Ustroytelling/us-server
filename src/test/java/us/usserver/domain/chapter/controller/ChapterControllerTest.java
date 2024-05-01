package us.usserver.domain.chapter.controller;

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
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.dto.ChapterStatus;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.paragraph.ParagraphMother;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Rollback
@AutoConfigureMockMvc
@Transactional
class ChapterControllerTest {
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
    private AuthorityRepository authorityRepository;

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;
    private Member member;
    private Member newMember;
    private Author author;
    private Author newAuthor;
    private Novel novel;
    private Chapter chapter1;
    private Chapter chapter2;
    private Paragraph paragraph1;
    private Paragraph paragraph2;
    private Paragraph paragraph3;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        accessToken = tokenProvider.issueAccessToken(member.getId());
        refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(1));

        newMember = MemberMother.generateMember();
        newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        novel = NovelMother.generateNovel(author);
        chapter1 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter2 = ChapterMother.generateChapter(novel);
        chapter2.setPartForTest(2);
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);

        CommentMother.generateComment(newAuthor, novel, chapter1);

        paragraph1 = ParagraphMother.generateParagraph(author, chapter1);
        paragraph2 = ParagraphMother.generateParagraph(newAuthor, chapter1);
        paragraph1.setSequenceForTest(1);
        paragraph1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2.setSequenceForTest(2);
        paragraph2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        chapter1.getParagraphs().add(paragraph1);
        chapter1.getParagraphs().add(paragraph2);


        paragraph3 = ParagraphMother.generateParagraph(author, chapter2);
        paragraph3.setSequenceForTest(1);
        paragraph3.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        chapter2.getParagraphs().add(paragraph3);

        Authority authority1 = Authority.builder().author(newAuthor).novel(novel).build();


        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
        paragraphRepository.save(paragraph3);
        authorityRepository.save(authority1);
    }

    @Test
    @DisplayName("특정 소설의 n화를 조회하는 API TEST")
    void getChapterDetailInfo() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/chapter/" + novel.getId() + "/" + chapter1.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(chapter1.getTitle());
        chapter1.getParagraphs()
                .forEach(paragraph -> assertThat(resultString).contains(paragraph.getContent()));
    }

    @Test
    @DisplayName("특정 소설의 챕터를 생성 API TEST")
    void createChapter1() throws Exception {
        // given
        chapter1.setStatusForTest(ChapterStatus.COMPLETED);
        chapter2.setStatusForTest(ChapterStatus.COMPLETED);

        // when
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                        .post("/chapter/" + novel.getId())
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Authorization-Refresh", "Bearer " + refreshToken)
                        .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("success");
    }

    @Test
    @DisplayName("챕터 생성 실패(메인 작가가 아님) API TEST")
    void createChapter2() throws Exception {
        // given
        String newAccessToken = tokenProvider.issueAccessToken(newMember.getId());
        String newRefreshToken = tokenProvider.issueRefreshToken(newMember.getId());
        redisUtils.setDateWithExpiration(newRefreshToken, newMember.getId(), Duration.ofDays(1));

        chapter1.setStatusForTest(ChapterStatus.COMPLETED);
        chapter2.setStatusForTest(ChapterStatus.COMPLETED);

        // when
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/chapter/" + novel.getId())
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Authorization-Refresh", "Bearer " + newRefreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isBadRequest());
        assertThat(resultString).contains(ExceptionMessage.MAIN_AUTHOR_NOT_MATCHED);
    }

    @Test
    @DisplayName("챕터 생성 실패(이전 회차 작성중) API TEST")
    void createChapter3() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/chapter/" + novel.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isBadRequest());
        assertThat(resultString).contains(ExceptionMessage.PREVIOUS_CHAPTER_IS_IN_PROGRESS);
    }
}