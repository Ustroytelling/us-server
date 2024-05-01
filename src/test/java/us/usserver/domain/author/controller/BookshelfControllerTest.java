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
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.author.repository.ReadNovelRepository;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.entity.NovelLike;
import us.usserver.domain.novel.repository.NovelLikeRepository;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.paragraph.ParagraphMother;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class BookshelfControllerTest {
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
    private AuthorityRepository authorityRepository;
    @Autowired
    private ReadNovelRepository readNovelRepository;
    @Autowired
    private NovelLikeRepository novelLikeRepository;

    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;
    private Author author;
    private Member member;
    private Novel novel;
    private Chapter chapter;
    private Paragraph paragraph1;
    private Paragraph paragraph2;
    private Paragraph paragraph3;
    private Authority authority;

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
        paragraph1 = ParagraphMother.generateParagraph(author, chapter);
        paragraph2 = ParagraphMother.generateParagraph(author, chapter);
        paragraph3 = ParagraphMother.generateParagraph(author, chapter);
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
    @DisplayName("최근 본 소설이 없는 API TEST")
    void recentViewedNovels3() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/viewed")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains("[]"); // 빈 리스트 리턴
    }

    @Test
    @DisplayName("최근 본 소설 불러오기 API TEST")
    void recentViewedNovels2() throws Exception {
        // given
        ReadNovel readNovel = ReadNovel.builder()
                .readDate(LocalDateTime.now()).novel(novel).author(author).build();
        author.addReadNovel(readNovel);

        // when
        authorRepository.save(author);
        readNovelRepository.save(readNovel);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/viewed")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(novel.getTitle());
    }

    @Test
    @DisplayName("최근 본 소설 삭제 API TEST")
    @Transactional
    void deleteRecentViewedNovels() throws Exception {
        // given
        ReadNovel readNovel = ReadNovel.builder()
                .readDate(LocalDateTime.now()).novel(novel).author(author).build();
        author.addReadNovel(readNovel);

        // when
        authorRepository.save(author);
        ReadNovel savedReadNovel = readNovelRepository.save(readNovel);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/bookshelf/viewed/" + savedReadNovel.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains("success");
    }

    @Test
    @DisplayName("내가 만든 소설이 없는 API TEST")
    void createdNovels2() throws Exception {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        String newAccessToken = tokenProvider.issueAccessToken(newMember.getId());
        String newRefreshToken = tokenProvider.issueRefreshToken(newMember.getId());
        redisUtils.setDateWithExpiration(newRefreshToken, newMember.getId(), Duration.ofDays(1));

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/created")
                .header("Authorization", "Bearer " + newAccessToken)
                .header("Authorization-Refresh", "Bearer " + newRefreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains("[]");
    }

    @Test
    @DisplayName("내가 만든 소설 API TEST")
    void createdNovels() throws Exception {
        // given
        Novel newNovel = NovelMother.generateNovel(author);

        // when
        novelRepository.save(newNovel);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/created")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(newNovel.getTitle());
    }

    @Test
    void deleteCreatedNovels() {
    }

    @Test
    @DisplayName("내가 참여하고 있는 소설 조회 API TEST")
    void joinedNovels() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/joined")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(novel.getTitle());
    }

    @Test
    void deleteJoinedNovels() {
    }

    @Test
    @DisplayName("내가 좋아요 누른 소설 조회 API TEST")
    void likedNovels() throws Exception {
        // given
        NovelLike novelLike = NovelLike.builder().novel(novel).author(author).build();

        // when
        novelLikeRepository.save(novelLike);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/bookshelf/joined")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(novel.getTitle());
    }

    @Test
    @DisplayName("내가 좋아요 누른 소설 삭제 API TEST")
    void deleteLikedNovels() throws Exception {
        // given
        NovelLike novelLike = NovelLike.builder().novel(novel).author(author).build();

        // when
        novelLikeRepository.save(novelLike);
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .delete("/bookshelf/joined")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).doesNotContain(novel.getTitle());
    }
}