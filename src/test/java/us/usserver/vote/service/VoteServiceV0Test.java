package us.usserver.vote.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import us.usserver.domain.member.entity.Author;
import us.usserver.author.AuthorMother;
import us.usserver.domain.member.repository.AuthorRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.chapter.ChapterMother;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.global.exception.AuthorNotAuthorizedException;
import us.usserver.global.exception.DuplicatedVoteException;
import us.usserver.global.exception.VoteNotFoundException;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.paragraph.service.VoteServiceV0;
import us.usserver.member.MemberMother;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.Novel;
import us.usserver.novel.NovelMother;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.paragraph.ParagraphMother;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.entity.Vote;
import us.usserver.domain.paragraph.repository.VoteJpaRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Rollback
@SpringBootTest
class VoteServiceV0Test {
    @Autowired
    private VoteServiceV0 voteServiceV0;

    @Autowired
    private VoteJpaRepository voteJpaRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Author author;
    private Novel novel;
    private Chapter chapter;
    private Paragraph paragraph;

    @BeforeEach
    void setUp() {
        author = AuthorMother.generateAuthor();
        setMember(author);
        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);
        paragraph = ParagraphMother.generateParagraph(author, chapter);
        paragraph.setSequenceForTest(0);

        novel.getChapters().add(chapter);

        authorRepository.save(author);
        novelRepository.save(novel);
        chapterRepository.save(chapter);
        paragraphRepository.save(paragraph);
    }

    @Test
    @DisplayName("마음에 드는 한줄에 투표하기")
    void voting() {
        // given

        // when
        voteServiceV0.voting(paragraph.getId(), author.getId());
        List<Vote> allByAuthor = voteJpaRepository.findAllByAuthor(author);

        // then
        assertThat(allByAuthor.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("마음에 드는 한줄에 투표하기 - 중복 투표 방지")
    void voting2() {
        // given

        // when
        voteServiceV0.voting(paragraph.getId(), author.getId());

        // then
        assertThrows(DuplicatedVoteException.class,
                () -> voteServiceV0.voting(paragraph.getId(), author.getId()));
    }

    @Test
    @DisplayName("투표 취소")
    void unvoting1() {
        // given
        Vote vote = Vote.builder().author(author).paragraph(paragraph).build();

        // when
        voteJpaRepository.save(vote);
        voteServiceV0.unvoting(vote.getId(), author.getId());
        List<Vote> allByAuthor = voteJpaRepository.findAllByAuthor(author);

        // then
        assertThat(allByAuthor.size()).isZero();
    }

    @Test
    @DisplayName("투표 취소 - 투표한 본인이 아닌 경우")
    void unvoting2() {
        // given
        Author newAuthor = AuthorMother.generateAuthor();
        setMember(newAuthor);

        Vote vote = Vote.builder().author(author).paragraph(paragraph).build();

        // when
        authorRepository.save(newAuthor);
        voteJpaRepository.save(vote);

        // then
        assertThrows(AuthorNotAuthorizedException.class,
                () -> voteServiceV0.unvoting(vote.getId(), newAuthor.getId()));
    }

    @Test
    @DisplayName("투표 취소 - 중복해서 투표 취소가 요청 될 때")
    void unvoting3() {
        // given
        Vote vote = Vote.builder().author(author).paragraph(paragraph).build();

        // when
        voteJpaRepository.save(vote);
        assertDoesNotThrow(
                () -> voteServiceV0.unvoting(vote.getId(), author.getId()));

        // then
        assertThrows(VoteNotFoundException.class,
                () -> voteServiceV0.unvoting(vote.getId(), author.getId()));
    }

    private void setMember(Author author) {
        Member member = MemberMother.generateMember();
        memberRepository.save(member);
        author.setMember(member);
    }
}