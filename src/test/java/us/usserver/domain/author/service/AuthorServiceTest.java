package us.usserver.domain.author.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.author.dto.ParagraphPreview;
import us.usserver.domain.author.dto.req.UpdateAuthorReq;
import us.usserver.domain.author.dto.res.GetParagraphNote;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.author.service.NoteService;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.NovelMother;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.ParagraphLike;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.repository.VoteRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@Transactional
@SpringBootTest
class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;

    Member member;
    Author author;


    @BeforeEach
    void setUp() {
        member = MemberMother.generateMember();
        author = AuthorMother.generateAuthorWithMember(member);
        member.setAuthor(author);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("사용자 정보 수정")
    void updateAuthor() {
        // given
        UpdateAuthorReq updateAuthorReq = UpdateAuthorReq.builder()
                .profileImg("PROFILE_IMG_URL")
                .nickname("임의의 닉네임")
                .introduction("임의의 소갯말")
                .participateNovelsPublic(Boolean.FALSE)
                .collectionNovelsPublic(Boolean.FALSE)
                .build();

        // when
        authorService.updateAuthor(member.getId(), updateAuthorReq);
        Optional<Author> changedAuthor = authorRepository.findById(author.getId());

        // then
        assertTrue(changedAuthor.isPresent());
        assertThat(changedAuthor.get().getProfileImg()).isEqualTo("PROFILE_IMG_URL");
        assertThat(changedAuthor.get().getNickname()).isEqualTo("임의의 닉네임");
        assertThat(changedAuthor.get().getIntroduction()).isEqualTo("임의의 소갯말");
        assertThat(changedAuthor.get().getParticipateNovelsPublic()).isFalse();
        assertThat(changedAuthor.get().getCollectionNovelsPublic()).isFalse();
    }

    @Test
    @DisplayName("폰트 사이즈 수정")
    void changeFontSize_1() {
        // given

        // when then
        assertDoesNotThrow(() -> authorService.changeFontSize(member.getId(), 10));
    }

    @Test
    @DisplayName("폰트 사이즈 수정 실패(범위 밖)")
    void changeFontSize_2() {
        // given

        // when then
        BaseException baseException = assertThrows(BaseException.class,
                () -> authorService.changeFontSize(member.getId(), 500));

        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.FONT_SIZE_OUT_OF_RANGE);
    }

    @Test
    @DisplayName("단락 간격 수정")
    void changeParagraphSpace_1() {
        // given

        // when then
        assertDoesNotThrow(() -> authorService.changeParagraphSpace(member.getId(), 10));
    }

    @Test
    @DisplayName("단락 간격 수정 실패(범위 밖)")
    void changeParagraphSpace_2() {
        // given

        // when then
        BaseException baseException = assertThrows(BaseException.class,
                () -> authorService.changeParagraphSpace(member.getId(), 50));

        assertThat(baseException.getMessage()).isEqualTo(ExceptionMessage.PARAGRAPH_SPACE_OUT_OF_RANGE);
    }
}