package us.usserver.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.utils.RedisUtils;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Rollback
@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerTest {
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
    @DisplayName("구독 연결 테스트")
    void subscribe() throws Exception {
        // given
        String subscribeUrl = "/notification/subscribe";

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .get(subscribeUrl)
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .accept(MediaType.TEXT_EVENT_STREAM));

        // then
        resultActions.andExpect(MockMvcResultMatchers.status().isOk());
        resultActions.andExpect(MockMvcResultMatchers.header().string(HttpHeaders.TRANSFER_ENCODING, "chunked"));
        resultActions.andDo(MockMvcResultHandlers.print());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        String contentType = resultActions.andReturn().getResponse().getContentType();
        assertTrue(contentType.contains("text/event-stream"));
        assertTrue(responseBody.contains("event:sse\ndata:EventStream Created. [receiverId ="));
    }
}
