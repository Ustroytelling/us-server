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
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.repository.VoteRepository;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class VoteControllerTest {
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
    private VoteRepository voteRepository;

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;

    private Member member;
    private Author author;
    private Novel novel;
    private Chapter chapter;
    private Paragraph paragraph1;
    private Paragraph paragraph2;
    private Paragraph paragraph3;
    private Paragraph paragraph4;
    private Paragraph paragraph5;

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

        paragraph1 = ParagraphMother.generateParagraph(author, chapter);
        paragraph2 = ParagraphMother.generateParagraph(author, chapter);
        paragraph3 = ParagraphMother.generateParagraph(author, chapter);
        paragraph4 = ParagraphMother.generateParagraph(author, chapter);
        paragraph5 = ParagraphMother.generateParagraph(author, chapter);
        paragraph1.setSequenceForTest(1);
        paragraph2.setSequenceForTest(2);
        paragraph3.setSequenceForTest(3);
        paragraph4.setSequenceForTest(4);
        paragraph5.setSequenceForTest(4);
        paragraph1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph3.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph4.setParagraphStatusForTest(ParagraphStatus.IN_VOTING);
        paragraph5.setParagraphStatusForTest(ParagraphStatus.IN_VOTING);
        chapter.getParagraphs().add(paragraph1);
        chapter.getParagraphs().add(paragraph2);
        chapter.getParagraphs().add(paragraph3);
        chapter.getParagraphs().add(paragraph4);
        chapter.getParagraphs().add(paragraph5);

        authorRepository.save(author);
        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
        paragraphRepository.save(paragraph3);
        paragraphRepository.save(paragraph4);
        paragraphRepository.save(paragraph5);
    }

    @Test
    @DisplayName("투표 하기 api test")
    void voting() throws Exception {
        // given

        // when
        List<Vote> prevVotes = voteRepository.findAllByAuthor(author);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/vote/" + paragraph4.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        List<Vote> nextVotes = voteRepository.findAllByAuthor(author);

        // then
        boolean anyMatch = nextVotes.stream().anyMatch(vote -> vote.getParagraph().getId().equals(paragraph4.getId()));
        assertThat(anyMatch).isTrue();
        assertThat(prevVotes.size()).isLessThan(nextVotes.size());
    }

    @Test
    @DisplayName("중복 투표 api test")
    void voting2() throws Exception {
        // given

        // when
        ResultActions resultActions1 = mockMvc.perform(MockMvcRequestBuilders
                .post("/vote/" + paragraph4.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        ResultActions resultActions2 = mockMvc.perform(MockMvcRequestBuilders
                .post("/vote/" + paragraph5.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions1.andExpect(status().isOk());
        resultActions2.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("투표 취소 하기 api test")
    void cancelVote() throws Exception {
        // given
        Vote vote = Vote.builder().paragraph(paragraph5).author(author).build();
        paragraph5.addVote(vote);

        // when
        voteRepository.save(vote);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/vote/" + paragraph5.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        List<Vote> votes = voteRepository.findAllByAuthor(author);

        // then
        resultActions.andExpect(status().isOk());
        votes.forEach(v -> assertThat(v.getParagraph()).isNotEqualTo(paragraph5));
    }
}