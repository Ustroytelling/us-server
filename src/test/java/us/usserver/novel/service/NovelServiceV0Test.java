package us.usserver.novel.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import us.usserver.author.Author;
import us.usserver.author.AuthorRepository;
import us.usserver.global.exception.NovelNotFoundException;
import us.usserver.novel.Novel;
import us.usserver.novel.NovelRepository;
import us.usserver.novel.dto.DetailInfoResponse;
import us.usserver.novel.dto.NovelInfoResponse;
import us.usserver.novel.novelEnum.AgeRating;
import us.usserver.novel.novelEnum.Genre;
import us.usserver.novel.novelEnum.Hashtag;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NovelServiceV0Test {
    @Autowired
    private NovelRepository novelRepository;
    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private NovelServiceV0 novelServiceV0;

    private Novel novel;
    private Author author;

    @BeforeEach
    void setup() {
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
    @DisplayName("소설 정보 확인")
    void getNovelInfo() {
        NovelInfoResponse novelInfoResponse = assertDoesNotThrow(
                () -> novelServiceV0.getNovelInfo(1L));

        assertThat(novelInfoResponse.getTitle()).isEqualTo(novel.getTitle());
        assertThat(novelInfoResponse.getCreatedAuthor().getId()).isEqualTo(novel.getAuthor().getId());
        assertThat(novelInfoResponse.getGenre()).isEqualTo(novel.getGenre());
        assertThat(novelInfoResponse.getHashtag()).isEqualTo(novel.getHashtag());

        assertThat(novelInfoResponse.getNovelSharelUrl()).contains("/novel/" + (1L));
        assertThat(novelInfoResponse.getDetailNovelInfoUrl()).contains("novel/" + (1L) + "/detail");
    }

    @Test
    @DisplayName("존재하지 않는 소설 정보 확인")
    void getNotExistNovel() {
        assertThrows(NovelNotFoundException.class,
                () -> novelServiceV0.getNovelInfo(2L));
    }

    @Test
    @DisplayName("소설에 참여한 작가 확인")
    void getNovelJoinedAuthor() {

    }

    @Test
    @DisplayName("소설에 달린 댓글 확인")
    void getNovelComment() {

    }

    @Test
    @DisplayName("소설 상세 정보 확인")
    void getNovelDetailInfo() {
        DetailInfoResponse detailInfoResponse = assertDoesNotThrow(
                () -> novelServiceV0.getNovelDetailInfo(1L));

        assertThat(detailInfoResponse.getTitle()).isEqualTo(novel.getTitle());
        assertThat(detailInfoResponse.getThumbnail()).isEqualTo(novel.getThumbnail());
        assertThat(detailInfoResponse.getSynopsis()).isEqualTo(novel.getSynopsis());
        assertThat(detailInfoResponse.getAuthorName()).isEqualTo(novel.getAuthor().getNickname());
        assertThat(detailInfoResponse.getAuthorIntroduction()).isEqualTo(novel.getAuthorDescription());
        assertThat(detailInfoResponse.getAgeRating()).isEqualTo(novel.getAgeRating());
        assertThat(detailInfoResponse.getGenre()).isEqualTo(novel.getGenre());
        assertThat(detailInfoResponse.getHashtags()).isEqualTo(novel.getHashtag());
        assertThat(detailInfoResponse.getStakeInfos()).isEqualTo(Collections.emptyList());
    }
}