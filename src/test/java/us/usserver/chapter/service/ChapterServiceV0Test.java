package us.usserver.chapter.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.author.Author;
import us.usserver.author.AuthorRepository;
import us.usserver.chapter.Chapter;
import us.usserver.chapter.ChapterRepository;
import us.usserver.chapter.dto.ChapterDetailRes;
import us.usserver.chapter.dto.ChaptersOfNovel;
import us.usserver.chapter.dto.CreateChapterReq;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelRepository;
import us.usserver.novel.novelEnum.AgeRating;
import us.usserver.novel.novelEnum.Genre;
import us.usserver.novel.novelEnum.Hashtag;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ChapterServiceV0Test {
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private ChapterServiceV0 chapterServiceV0;
    @Autowired
    private ChapterRepository chapterRepository;

    private Novel novel;
    private Author author;

    @BeforeEach
    void setUp() {
        Set<Hashtag> hashtags = new HashSet<>();
        hashtags.add(Hashtag.HASHTAG1);
        hashtags.add(Hashtag.HASHTAG2);
        hashtags.add(Hashtag.MONCHKIN);

        author = Author.builder()
                .id(1L)
                .nickname("NICKNAME")
                .introduction("INTRODUCTION")
                .profileImg("PROFILE_IMG")
                .build();
        authorRepository.save(author);

        novel = Novel.builder()
                .id(1L)
                .title("TITLE")
                .thumbnail("THUMBNAIL")
                .synopsis("SYNOPSIS")
                .author(author)
                .authorDescription("AUTHOR_DESCRIPTION")
                .hashtag(hashtags)
                .genre(Genre.FANTASY)
                .ageRating(AgeRating.GENERAL)
                .build();
        novelRepository.save(novel);
    }

    @Test
    @DisplayName("회차 생성")
    void createChapter() {
        CreateChapterReq createChapterReq1 = CreateChapterReq.builder()
                .title("첫번 째 이야기")
                .build();

        assertDoesNotThrow(
                () -> chapterServiceV0.createChapter(1L, createChapterReq1));
    }

    @Test
    @DisplayName("소설 회차 정보 조회")
    void getChaptersOfNovel() {
        CreateChapterReq createChapterReq1 = CreateChapterReq.builder()
                .title("두번 째 이야기")
                .build();
        CreateChapterReq createChapterReq2 = CreateChapterReq.builder()
                .title("세번 째 이야기")
                .build();
        assertDoesNotThrow(
                () -> {
                    chapterServiceV0.createChapter(1L, createChapterReq1);
                    chapterServiceV0.createChapter(1L, createChapterReq2);
                }
        );

        List<ChaptersOfNovel> chaptersOfNovels = assertDoesNotThrow(
                () -> chapterServiceV0.getChaptersOfNovel(1L));

        assertThat(chaptersOfNovels.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("회차 상세 정보 조회")
    void getChapterDetail() {
        CreateChapterReq createChapterReq = CreateChapterReq.builder()
                .title("마지막 이야기")
                .build();
        Assertions.assertDoesNotThrow(
                () -> chapterServiceV0.createChapter(1L, createChapterReq));
        Optional<Chapter> chapter = chapterRepository.getChapterByTitle("마지막 이야기");
        Long chapterId = chapter.get().getId();


        ChapterDetailRes chapterDetail = chapterServiceV0.getChapterDetail(1L, chapterId);
        org.assertj.core.api.Assertions.assertThat()

    }
}