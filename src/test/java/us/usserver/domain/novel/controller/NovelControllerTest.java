package us.usserver.domain.novel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.constant.AgeRating;
import us.usserver.domain.novel.constant.Genre;
import us.usserver.domain.novel.constant.Hashtag;
import us.usserver.domain.novel.constant.NovelSize;
import us.usserver.domain.novel.dto.AuthorDescription;
import us.usserver.domain.novel.dto.MainNovelType;
import us.usserver.domain.novel.dto.req.NovelBlueprint;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class NovelControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

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
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;

    private Member member;
    private Author author;
    private Novel novel;
    private Chapter chapter1;
    private Chapter chapter2;
    private Chapter chapter3;

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
        chapter1 = ChapterMother.generateChapter(novel);
        chapter1.setPartForTest(1);
        chapter2 = ChapterMother.generateChapter(novel);
        chapter2.setPartForTest(2);
        chapter3 = ChapterMother.generateChapter(novel);
        chapter3.setPartForTest(3);
        novel.getChapters().add(chapter1);
        novel.getChapters().add(chapter2);
        novel.getChapters().add(chapter3);

        novelRepository.save(novel);
        chapterRepository.save(chapter1);
        chapterRepository.save(chapter2);
        chapterRepository.save(chapter3);
    }

    @Test
    @DisplayName("소설 생성 API TEST")
    void createNovel() throws Exception {
        // given
        NovelBlueprint novelBluePrint = NovelBlueprint.builder()
                .title("멋진 소설 제목")
                .synopsis("멋진 소설의 멋진 시놉시스를 작성해줘")
                .authorDescription("멋진 소설의 멋진 작가 소개")
                .hashtag(Set.of(Hashtag.판타지))
                .genre(Genre.드라마)
                .ageRating(AgeRating.GENERAL)
                .novelSize(NovelSize.SHORT)
                .thumbnail("멋진 썸네일")
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(novelBluePrint);


        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/novel")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isCreated());

    }

    @Test
    @DisplayName("소설 정보 조회 API TEST")
    void getNovelInfo() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel/" + novel.getId())
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(novel.getTitle());
        assertThat(resultString).contains(novel.getGenre().toString());
        novel.getHashtags().forEach(hashtag ->
                assertThat(resultString).contains(hashtag.toString()));
        assertThat(resultString).contains("joinedAuthorCnt\":");
        assertThat(resultString).contains("likeCnt\":");
    }

    @Test
    @DisplayName("소설 상세 정보 조회 API TEST")
    void getNovelDetailInfo() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel/" + novel.getId() + "/detail")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        assertThat(resultString).contains(novel.getTitle());
        assertThat(resultString).contains(novel.getThumbnail());
        assertThat(resultString).contains(novel.getSynopsis());
        assertThat(resultString).contains(author.getNickname());
        assertThat(resultString).contains(novel.getAgeRating().toString());
        assertThat(resultString).contains(novel.getGenre().toString());
        novel.getHashtags().forEach(hashtag ->
                assertThat(resultString).contains(hashtag.toString()));
        assertThat(resultString).contains(chapter1.getTitle());
        assertThat(resultString).contains(chapter2.getTitle());
        assertThat(resultString).contains(chapter3.getTitle());
    }

    @Test
    @DisplayName("소설 줄거리 수정 API TEST")
    void modifyNovelSynopsis() throws Exception {
        // given
        Map<String, String> requestBody = new HashMap<>();
        String key = "synopsis", value = "흐으으으으음... 이것이 창작의 고통인가..";
        requestBody.put(key, value);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/novel/" + novel.getId() + "/synopsis")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(requestBody)));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isCreated());
        assertThat(resultString).contains(value);
    }

    @Test
    @DisplayName("작가 소개 수정 API TEST")
    void modifyAuthorDescription() throws Exception {
        // given
        AuthorDescription req = AuthorDescription.builder().description("작가 소개 업데이트").build();
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(req);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/novel/" + novel.getId() + "/author-description")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(reqBody));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("작가 소개 업데이트");
    }

    @Test
    @DisplayName("메인 페이지 조회 API TEST")
    void getHomeNovelListInfo() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel/" + "main")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("popularNovels");
        assertThat(resultString).contains("readNovels");
        assertThat(resultString).contains("realTimeUpdateNovels");
        assertThat(resultString).contains("recentlyCreatedNovels");
    }

    @Test
    @DisplayName("소설 더보기 조회 API TEST")
    void getMoreNovels() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel" + "/main/more" + "/" + MainNovelType.NEW + "/0")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("novelList");
        assertThat(resultString).contains("nextPage");
        assertThat(resultString).contains("hasNext");
    }

    @Test
    @DisplayName("읽은 소설 더보기 API TEST")
    void readNovel() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel" + "/main/more/read")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("novelList");
        assertThat(resultString).contains("nextPage");
        assertThat(resultString).contains("hasNext");
    }

    @Test
    @DisplayName("소설 검색 API TEST")
    void searchNovel() throws Exception {
        // given

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get("/novel" + "/search" + "/0")
                .param("keyword", "검색어")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .contentType(MediaType.APPLICATION_JSON));
        String resultString = resultActions.andReturn().getResponse().getContentAsString();

        // then
        resultActions.andExpect(status().isOk());
        assertThat(resultString).contains("novelSimpleInfos");
        assertThat(resultString).contains("nextPage");
        assertThat(resultString).contains("hasNext");
    }
}