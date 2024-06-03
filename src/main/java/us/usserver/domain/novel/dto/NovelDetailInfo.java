package us.usserver.domain.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import us.usserver.domain.chapter.dto.ChapterInfo;
import us.usserver.domain.novel.constant.AgeRating;
import us.usserver.domain.novel.constant.Genre;
import us.usserver.domain.novel.constant.Hashtag;
import us.usserver.domain.authority.dto.StakeInfo;

import java.util.List;
import java.util.Set;

@Builder
public record NovelDetailInfo (
        @Schema(description = "소설 제목", example = "주술 회전")
        String title,

        @Schema(description = "소설 썸네일", nullable = true, example = "https:// ~")
        String thumbnail,

        @Schema(description = "소설 줄거리",example = "옛날 옛날 아주 머언 옛낫...")
        String synopsis,

        @Schema(description = "메인 작가 이름",example = "강아지상")
        String authorName,

        @Schema(description = "메인 작가 ID ",example = "1")
        Long authorId,

        @Schema(description = "메인 작가 소개",example = "안녕하세요. 메인자갑니다.")
        String authorIntroduction,

        @Schema(description = "이용가",example = "GENERAL, TWELVE_PLUS, FIFTEEN_PLUS,")
        AgeRating ageRating,

        @Schema(description = "소설 장르", example = "FANTASY")
        Genre genre,

        @Schema(description = "해시태그",example = "[HASHTAG1, HASHTAG2, ...]")
        Set<Hashtag> hashtags,

        @Schema(description = "지분 정보", nullable = true, example = "[{}, {}, ...]")
        List<StakeInfo> stakeInfos,

        @Schema(description = "회차 정보", example = "[{}, {}, ...]")
        List<ChapterInfo> chapterInfos,

        @Schema(description = "좋아요 유무", example = "true or false")
        Boolean isLiked
) {}
