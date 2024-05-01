package us.usserver.domain.author.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.AuthorMother;
import us.usserver.domain.chapter.ChapterMother;
import us.usserver.domain.author.dto.res.BookshelfDefaultResponse;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.author.entity.ReadNovel;
import us.usserver.domain.author.repository.AuthorRepository;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.repository.ChapterRepository;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.repository.MemberRepository;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.entity.NovelLike;
import us.usserver.domain.novel.repository.NovelLikeRepository;
import us.usserver.domain.novel.repository.NovelRepository;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;
import us.usserver.domain.member.MemberMother;
import us.usserver.domain.novel.NovelMother;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Rollback
@Transactional
@SpringBootTest
class BookshelfServiceTest {
    @Autowired
    private BookshelfService bookshelfService;

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private NovelLikeRepository novelLikeRepository;

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

        novel = NovelMother.generateNovel(author);
        chapter = ChapterMother.generateChapter(novel);

        novel.getChapters().add(chapter);
        novelRepository.save(novel);
        chapterRepository.save(chapter);
        authorityRepository.save(Authority.builder().novel(novel).author(author).build());
    }

    @AfterEach
    void setOff() {
        author.getReadNovels().clear();
    }

    @Test
    @DisplayName("최근 본 소설 조회")
    void recentViewedNovels() {
        // given
        Novel newNovel = NovelMother.generateNovel(author);
        Chapter newChapter = ChapterMother.generateChapter(novel);
        newNovel.getChapters().add(newChapter);

        // when
        novelRepository.save(newNovel);
        chapterRepository.save(newChapter);
        author.addReadNovel(ReadNovel.builder().author(author).novel(novel).readDate(LocalDateTime.now()).build());
        author.addReadNovel(ReadNovel.builder().author(author).novel(newNovel).readDate(LocalDateTime.now()).build());
        authorRepository.save(author);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.recentViewedNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(2);
    }


    @Test
    @DisplayName("최근 본 소설 조회 - 최근 본 소설 없음")
    void recentViewedNovels2() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        // when
        authorRepository.save(newAuthor);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.recentViewedNovels(newAuthor.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isZero();
    }

    @Test
    @DisplayName("내가 생성한 소설 조회")
    void createdNovels() {
        // given

        // when
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.createdNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(1);
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).title()).isEqualTo(novel.getTitle());
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).mainAuthor().nickname()).isEqualTo(author.getNickname());
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).joinedAuthor()).isEqualTo(1);
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).shortcuts()).contains(novel.getId().toString());
    }

    @Test
    @DisplayName("내가 생성한 소설 조회 - 0개")
    void createdNovels1() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        // when
        authorRepository.save(newAuthor);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.createdNovels(newAuthor.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isZero();
    }

    @Test
    @DisplayName("내가 생성한 소설 조회 - 2개 이상")
    void createdNovels2() {
        // given
        Novel newNovel1 = NovelMother.generateNovel(author);
        Novel newNovel2 = NovelMother.generateNovel(author);
        Novel newNovel3 = NovelMother.generateNovel(author);

        // when
        novelRepository.save(newNovel1);
        novelRepository.save(newNovel2);
        novelRepository.save(newNovel3);
        authorityRepository.save(Authority.builder().novel(newNovel1).author(author).build());
        authorityRepository.save(Authority.builder().novel(newNovel2).author(author).build());
        authorityRepository.save(Authority.builder().novel(newNovel3).author(author).build());
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.createdNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("내가 생성한 소설 조회 - 삭제된 소설")
    void createdNovels3() {
        // given
        Novel newNovel1 = NovelMother.generateNovel(author);
        Novel newNovel2 = NovelMother.generateNovel(author);
        Novel newNovel3 = NovelMother.generateNovel(author);
        Authority authority1 = Authority.builder().novel(newNovel1).author(author).build();
        Authority authority2 = Authority.builder().novel(newNovel2).author(author).build();
        Authority authority3 = Authority.builder().novel(newNovel3).author(author).build();

        // when
        novelRepository.save(newNovel1);
        novelRepository.save(newNovel2);
        novelRepository.save(newNovel3);
        authorityRepository.save(authority1);
        authorityRepository.save(authority2);
        authorityRepository.save(authority3);
        BookshelfDefaultResponse before = bookshelfService.createdNovels(author.getId());

        authorityRepository.delete(authority1);
        authorityRepository.delete(authority2);
        authorityRepository.delete(authority3);
        novelRepository.delete(newNovel1);
        novelRepository.delete(newNovel2);
        novelRepository.delete(newNovel3);
        BookshelfDefaultResponse after = bookshelfService.createdNovels(author.getId());

        // then
        assertThat(before.novelPreviews().size()).isEqualTo(4);
        assertThat(after.novelPreviews().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("내가 참여 중인 소설 조회")
    void joinedNovels() {
        // given

        // when
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.joinedNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(1);
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).title()).isEqualTo(novel.getTitle());
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).thumbnail()).isEqualTo(novel.getThumbnail());
    }

    @Test
    @DisplayName("내가 참여 중인 소설 조회 - 0개")
    void joinedNovels1() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);

        // when
        authorRepository.save(newAuthor);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.joinedNovels(newAuthor.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isZero();
    }

    @Test
    @DisplayName("내가 참여 중인 소설 조회 - 2개 이상")
    void joinedNovels2() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);
        Novel newNovel1 = NovelMother.generateNovel(newAuthor);
        Novel newNovel2 = NovelMother.generateNovel(newAuthor);

        // when
        authorRepository.save(newAuthor);
        novelRepository.save(newNovel1);
        novelRepository.save(newNovel2);
        authorityRepository.save(Authority.builder().novel(novel).author(newAuthor).build());
        authorityRepository.save(Authority.builder().novel(newNovel1).author(newAuthor).build());
        authorityRepository.save(Authority.builder().novel(newNovel2).author(newAuthor).build());
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.joinedNovels(newAuthor.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("내가 참여 중인 소설 조회 - 삭제된 소설")
    void joinedNovels3() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);
        newMember.setAuthor(newAuthor);
        memberRepository.save(newMember);
        Novel newNovel1 = NovelMother.generateNovel(newAuthor);
        Novel newNovel2 = NovelMother.generateNovel(newAuthor);
        Authority authority1 = Authority.builder().novel(newNovel1).author(newAuthor).build();
        Authority authority2 = Authority.builder().novel(newNovel1).author(newAuthor).build();

        // when
        authorRepository.save(newAuthor);
        novelRepository.save(newNovel1);
        novelRepository.save(newNovel2);
        authorityRepository.save(authority1);
        authorityRepository.save(authority2);
        BookshelfDefaultResponse before = bookshelfService.joinedNovels(newAuthor.getId());

        authorityRepository.delete(authority1);
        authorityRepository.delete(authority2);
        novelRepository.delete(newNovel1);
        novelRepository.delete(newNovel2);
        BookshelfDefaultResponse after = bookshelfService.joinedNovels(newAuthor.getId());
        
        // then
        assertThat(before.novelPreviews().size()).isEqualTo(2);
        assertThat(after.novelPreviews().size()).isZero();
    }


    @Test
    @DisplayName("좋아요 누른 소설 조회")
    void likedNovels() {
        // given
        NovelLike like = NovelLike.builder().novel(novel).author(author).build();

        // when
        novelLikeRepository.save(like);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.likedNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(1);
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).title()).isEqualTo(novel.getTitle());
        assertThat(bookshelfDefaultResponse.novelPreviews().get(0).thumbnail()).isEqualTo(novel.getThumbnail());
    }

    @Test
    @DisplayName("좋아요 누른 소설 조회 - 0개")
    void likedNovels1() {
        // given

        // when
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.likedNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isZero();
    }


    @Test
    @DisplayName("좋아요 누른 소설 조회 - 2개 이상")
    void likedNovels2() {
        // given
        Novel newNovel1 = NovelMother.generateNovel(author);
        Novel newNovel2 = NovelMother.generateNovel(author);
        NovelLike like1 = NovelLike.builder().novel(newNovel1).author(author).build();
        NovelLike like2 = NovelLike.builder().novel(newNovel2).author(author).build();

        // when
        novelRepository.save(newNovel1);
        novelRepository.save(newNovel2);
        novelLikeRepository.save(like1);
        novelLikeRepository.save(like2);
        BookshelfDefaultResponse bookshelfDefaultResponse = bookshelfService.likedNovels(author.getId());

        // then
        assertThat(bookshelfDefaultResponse.novelPreviews().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("좋아요 누른 소설 조회 - 좋아요 누른 소설 삭제")
    void likedNovels3() {
        // given
        Novel newNovel = NovelMother.generateNovel(author);
        NovelLike like = NovelLike.builder().novel(newNovel).author(author).build();

        // when
        novelRepository.save(newNovel);
        novelLikeRepository.save(like);
        BookshelfDefaultResponse before = bookshelfService.likedNovels(author.getId());

        novelLikeRepository.delete(like);
        novelRepository.delete(newNovel);
        BookshelfDefaultResponse after = bookshelfService.likedNovels(author.getId());

        // then
        assertThat(before.novelPreviews().size()).isEqualTo(1);
        assertThat(after.novelPreviews().size()).isZero();
    }

    @Test
    @DisplayName("모든 책장 조회 기능 중 - 존재하지 않는 사용자")
    void recentViewedNovels1() {
        // given
        Member newMember = MemberMother.generateMember();
        Author newAuthor = AuthorMother.generateAuthorWithMember(newMember);

        // when
        BaseException baseException1 = assertThrows(BaseException.class,
                () -> bookshelfService.recentViewedNovels(newAuthor.getId()));
        BaseException baseException2 = assertThrows(BaseException.class,
                () -> bookshelfService.createdNovels(newAuthor.getId()));
        BaseException baseException3 = assertThrows(BaseException.class,
                () -> bookshelfService.joinedNovels(newAuthor.getId()));
        BaseException baseException4 = assertThrows(BaseException.class,
                () -> bookshelfService.likedNovels(newAuthor.getId()));

        // then
        assertThat(baseException1.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);
        assertThat(baseException2.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);
        assertThat(baseException3.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);
        assertThat(baseException4.getMessage()).isEqualTo(ExceptionMessage.AUTHOR_NOT_FOUND);

    }
}