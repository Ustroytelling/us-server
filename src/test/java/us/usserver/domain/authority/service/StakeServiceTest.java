package us.usserver.domain.authority.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.authority.dto.res.StakeInfoResponse;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.authority.dto.StakeInfo;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback
@Transactional
@SpringBootTest
class StakeServiceTest {
    @Autowired
    StakeService stakeService;

    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private MemberRepository memberRepository;

    Author mainAuthor, author1, author2, author3;
    Member mainMember, member1, member2, member3;
    Novel novel;
    Chapter chapter1, chapter2, chapter3;
    Paragraph paragraph1_1, paragraph2_1, paragraph2_2, paragraph3_1, paragraph3_2, paragraph3_3;

    @BeforeEach
    void setUp() {
        mainMember = MemberMother.generateMember();
        mainAuthor = AuthorMother.generateAuthorWithMember(mainMember);
        mainMember.setAuthor(mainAuthor);
        memberRepository.save(mainMember);

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


        novel = NovelMother.generateNovel(mainAuthor);
        chapter1 = ChapterMother.generateChapter(novel);
        chapter2 = ChapterMother.generateChapter(novel);
        chapter3 = ChapterMother.generateChapter(novel);
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);
        novelRepository.save(novel);

        authorityRepository.save(Authority.builder()
                .novel(novel).author(mainAuthor).build());
        authorityRepository.save(Authority.builder()
                .novel(novel).author(author1).build());
        authorityRepository.save(Authority.builder()
                .novel(novel).author(author2).build());
        authorityRepository.save(Authority.builder()
                .novel(novel).author(author3).build());

        paragraph1_1 = ParagraphMother.generateParagraph(author1, chapter1);
        paragraph2_1 = ParagraphMother.generateParagraph(author1, chapter2);
        paragraph3_1 = ParagraphMother.generateParagraph(author1, chapter3);
        paragraph2_2 = ParagraphMother.generateParagraph(author2, chapter2);
        paragraph3_2 = ParagraphMother.generateParagraph(author2, chapter3);
        paragraph3_3 = ParagraphMother.generateParagraph(author3, chapter3);
        chapter1.getParagraphs().add(paragraph1_1);
        chapter2.getParagraphs().add(paragraph2_1);
        chapter2.getParagraphs().add(paragraph2_2);
        chapter3.getParagraphs().add(paragraph3_1);
        chapter3.getParagraphs().add(paragraph3_2);
        chapter3.getParagraphs().add(paragraph3_3);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        chapterRepository.save(chapter3);

        paragraph1_1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2_1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph2_2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph3_1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph3_2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        paragraph3_3.setParagraphStatusForTest(ParagraphStatus.SELECTED);

        paragraphRepository.save(paragraph1_1);
        paragraphRepository.save(paragraph2_1);
        paragraphRepository.save(paragraph2_2);
        paragraphRepository.save(paragraph3_1);
        paragraphRepository.save(paragraph3_2);
        paragraphRepository.save(paragraph3_3);

        stakeService.setStakeInfoOfNovel(novel);

    }

    @Test
    @DisplayName("작가 한명만 계속 해서 소설을 업데이트 할 때 지분 100이 유지되는지 확인")
    void checkOneAuthorStake() {
        // given
        Novel novelForOne = NovelMother.generateNovel(author1);
        Chapter chapterForOne = ChapterMother.generateChapter(novelForOne);
        Paragraph p1 = ParagraphMother.generateParagraph(author1, chapterForOne);
        p1.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        Paragraph p2 = ParagraphMother.generateParagraph(author1, chapterForOne);
        p2.setParagraphStatusForTest(ParagraphStatus.SELECTED);
        Paragraph p3 = ParagraphMother.generateParagraph(author1, chapterForOne);
        p3.setParagraphStatusForTest(ParagraphStatus.SELECTED);

        // when
        novelForOne.getChapters().add(chapterForOne);
        novelRepository.save(novelForOne);

        chapterForOne.getParagraphs().add(p1);
        chapterForOne.getParagraphs().add(p2);
        chapterRepository.save(chapterForOne);

        paragraphRepository.save(p1);
        paragraphRepository.save(p2);
        paragraphRepository.save(p3);

        authorityRepository.save(Authority.builder()
                .novel(novelForOne).author(author1).build());
        stakeService.setStakeInfoOfNovel(novelForOne);

        // then
        StakeInfoResponse stakeInfoResponse = stakeService.getStakeInfoOfNovel(novelForOne.getId());
        assertThat(stakeInfoResponse.getStakeInfos().size()).isEqualTo(1);
        assertThat(stakeInfoResponse.getStakeInfos().get(0).getAuthorInfo().id()).isEqualTo(author1.getId());
        assertThat(stakeInfoResponse.getStakeInfos().get(0).getPercentage()).isEqualTo(1F);
    }

    @Test
    @DisplayName("여러 작가가 추가 되었을 때 지분 정보 확인")
    void getStakeInfoOfNovel() {
        // given
        StakeInfoResponse prevStakeResponse = stakeService.getStakeInfoOfNovel(novel.getId());

        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        Authority authority = Authority.builder().author(newAuthor).novel(novel).build();
        Paragraph newParagraph = ParagraphMother.generateParagraph(newAuthor, chapter3);
        newParagraph.setParagraphStatusForTest(ParagraphStatus.SELECTED);

        // when
        authorityRepository.save(authority);
        chapter3.addParagraph(newParagraph);
        paragraphRepository.save(newParagraph);
        stakeService.setStakeInfoOfNovel(novel);

        // then
        StakeInfoResponse curStakeResponse = stakeService.getStakeInfoOfNovel(novel.getId());
        assertThat(curStakeResponse.getStakeInfos().size()).isEqualTo(prevStakeResponse.getStakeInfos().size() + 1);

        for (StakeInfo prevStake : prevStakeResponse.getStakeInfos()) {
            for (StakeInfo curStake : curStakeResponse.getStakeInfos()) {
                if (curStake.getAuthorInfo() == prevStake.getAuthorInfo()) {
                    assertThat(prevStake.getPercentage()).isGreaterThanOrEqualTo(curStake.getPercentage());
                }
            }
        }
    }
}
