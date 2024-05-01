package us.usserver.domain.author.controller;

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
import us.usserver.domain.author.dto.req.FontSizeReq;
import us.usserver.domain.author.dto.req.ParagraphSpaceReq;
import us.usserver.domain.author.dto.req.UpdateAuthorReq;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.member.service.TokenProvider;
import us.usserver.global.utils.RedisUtils;

import java.time.Duration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Rollback
@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AuthorControllerTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MockMvc mockMvc;

    private String accessToken;
    private String refreshToken;
    private Author author;
    private Member member;

    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);

        accessToken = tokenProvider.issueAccessToken(member.getId());
        refreshToken = tokenProvider.issueRefreshToken(member.getId());
        redisUtils.setDateWithExpiration(refreshToken, member.getId(), Duration.ofDays(1));
    }

    @Test
    @DisplayName("사용자 정보 수정 API TEST")
    void updateAuthor_1() throws Exception {
        // given
        UpdateAuthorReq updateAuthorReq = UpdateAuthorReq.builder()
                .profileImg("PROFILE_IMG_URL")
                .nickname("임의의 닉네임")
                .introduction("임의의 소갯말")
                .participateNovelsPublic(Boolean.FALSE)
                .collectionNovelsPublic(Boolean.FALSE)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String reqBody = objectMapper.writeValueAsString(updateAuthorReq);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/author")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("기본 폰트 사이즈 수정 API TEST")
    void changeFontSize_1() throws Exception {
        // given
        FontSizeReq fontSizeReq = FontSizeReq.builder().fontSize(10).build();
        String reqBody = objectMapper.writeValueAsString(fontSizeReq);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/author/fontsize")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("기본 폰트 사이즈 수정 실패(범위 밖) API TEST")
    void changeFontSize_2() throws Exception {
        // given
        FontSizeReq fontSizeReq = FontSizeReq.builder().fontSize(50).build();
        String reqBody = objectMapper.writeValueAsString(fontSizeReq);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/author/fontsize")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("단락 간격 수정 API TEST")
    void changeParagraphSpace_1() throws Exception {
        // given
        ParagraphSpaceReq paragraphSpaceReq = ParagraphSpaceReq.builder().paragraphSpace(10).build();
        String reqBody = objectMapper.writeValueAsString(paragraphSpaceReq);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/author/paragraph-space")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    @DisplayName("단락 간격 수정 실패(범위 밖) API TEST")
    void changeParagraphSpace_2() throws Exception {
        // given
        ParagraphSpaceReq paragraphSpaceReq = ParagraphSpaceReq.builder().paragraphSpace(50).build();
        String reqBody = objectMapper.writeValueAsString(paragraphSpaceReq);

        // when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .patch("/author/paragraph-space")
                .header("Authorization", "Bearer " + accessToken)
                .header("Authorization-Refresh", "Bearer " + refreshToken)
                .content(reqBody)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}