package us.usserver.domain.authority.controller;

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
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.authority.service.StakeService;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.paragraph.ParagraphMother;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class StakeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StakeService stakeService;

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

    private Author author1, author2, author3, author4, author5;
    private Member member1, member2, member3, member4, member5;
    private Novel novel;
    private Chapter chapter1, chapter2;
    private Paragraph paragraph1, paragraph2, paragraph3, paragraph4, paragraph5, paragraph6, paragraph7, paragraph8;

    @BeforeEach
    void setUp() {
        member1 = MemberMother.generateMember();
        author1 = AuthorMother.generateAuthorWithMember(member1);
        member1.setAuthor(author1);
        memberRepository.save(member1);

        member2 = MemberMother.generateMember();
        author2 = AuthorMother.generateAuthorWithMember(member2);
        member2.setAuthor(author2);
        memberRepository.save(member2);

        member3 = MemberMother.generateMember();
        author3 = AuthorMother.generateAuthorWithMember(member3);
        member3.setAuthor(author3);
        memberRepository.save(member3);

        member4 = MemberMother.generateMember();
        author4 = AuthorMother.generateAuthorWithMember(member4);
        member4.setAuthor(author4);
        memberRepository.save(member4);

        member5 = MemberMother.generateMember();
        author5 = AuthorMother.generateAuthorWithMember(member5);
        member5.setAuthor(author5);
        memberRepository.save(member5);

        novel = NovelMother.generateNovel(author1);
        chapter1 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter2 = ChapterMother.generateChapter(novel);
        chapter2.setPartForTest(2);
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);

        paragraph1 = ParagraphMother.generateParagraph(author1, chapter1);
        paragraph2 = ParagraphMother.generateParagraph(author2, chapter1);
        paragraph3 = ParagraphMother.generateParagraph(author3, chapter1);
        paragraph1.setSequenceForTest(1);
        paragraph1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2.setSequenceForTest(2);
        paragraph2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph3.setSequenceForTest(3);
        paragraph3.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        chapter1.getParagraphs().add(paragraph1);
        chapter1.getParagraphs().add(paragraph2);
        chapter1.getParagraphs().add(paragraph3);

        paragraph4 = ParagraphMother.generateParagraph(author4, chapter2);
        paragraph5 = ParagraphMother.generateParagraph(author4, chapter2);
        paragraph6 = ParagraphMother.generateParagraph(author4, chapter2);
        paragraph7 = ParagraphMother.generateParagraph(author5, chapter2);
        paragraph8 = ParagraphMother.generateParagraph(author5, chapter2);
        paragraph4.setSequenceForTest(1);
        paragraph4.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph5.setSequenceForTest(2);
        paragraph5.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph6.setSequenceForTest(3);
        paragraph6.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph7.setSequenceForTest(4);
        paragraph7.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph8.setSequenceForTest(4);
        paragraph8.setParagraphStatusForTest(ParagraphStatus.UNSELECTED);
        chapter2.getParagraphs().add(paragraph4);
        chapter2.getParagraphs().add(paragraph5);
        chapter2.getParagraphs().add(paragraph6);
        chapter2.getParagraphs().add(paragraph7);
        chapter2.getParagraphs().add(paragraph8);

        Authority authority1 = Authority.builder().author(author1).novel(novel).build();
        Authority authority2 = Authority.builder().author(author2).novel(novel).build();
        Authority authority3 = Authority.builder().author(author3).novel(novel).build();
        Authority authority4 = Authority.builder().author(author4).novel(novel).build();
        Authority authority5 = Authority.builder().author(author5).novel(novel).build();

        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        paragraphRepository.save(paragraph1);
        paragraphRepository.save(paragraph2);
        paragraphRepository.save(paragraph3);
        paragraphRepository.save(paragraph4);
        paragraphRepository.save(paragraph5);
        paragraphRepository.save(paragraph6);
        paragraphRepository.save(paragraph7);
        paragraphRepository.save(paragraph8);
        authorityRepository.save(authority1);
        authorityRepository.save(authority2);
        authorityRepository.save(authority3);
        authorityRepository.save(authority4);
        authorityRepository.save(authority5);
    }

    @Test
    @DisplayName("지분 조회 API TEST")
    void getStakes() throws Exception {
        // given

        // when
        stakeService.setStakeInfoOfNovel(novel);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/stake/" + novel.getId())
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        Assertions.assertThat(resultString).contains(author1.getNickname());
        Assertions.assertThat(resultString).contains(author2.getNickname());
        Assertions.assertThat(resultString).contains(author3.getNickname());
        Assertions.assertThat(resultString).contains(author4.getNickname());
        Assertions.assertThat(resultString).contains(author5.getNickname());
    }
}