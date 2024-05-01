package us.usserver.domain.novel.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.constant.*;
import us.usserver.domain.novel.entity.Novel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

@Builder
public record NovelBlueprint(
        @Schema(description = "소설 제목", example = "주술회전")
        @Size(max = 16, min = 2)
        @NotBlank
        String title,

        @Schema(description = "소설 줄거리", example = "주술을 사용하며 싸우는 액션소설")
        @Size(max = 300)
        @NotBlank
        String synopsis,

        @Schema(description = "작가 소개", example = "액선과 판타지를 연재하고 있는 xxx입니다.")
        @Size(max = 300)
        @NotBlank
        String authorDescription,

        @Schema(description = "소설을 나타내는 태그", example = "#먼치킨, #병맛")
        @Size(max = 8)
        @NotNull
        Set<Hashtag> hashtag,

        @Schema(description = "소설 장르", example = "액션")
        @NotNull
        Genre genre,

        @Schema(description = "소설 연령 제한", example = "19세 이용가")
        @NotNull
        AgeRating ageRating,

        @Schema(description = "소설 크기 분류", example = "장편소설")
        @NotNull
        NovelSize novelSize,

        @Schema(description = "소설 썸네일", example = "주술회전.jpg")
        @NotBlank
        String thumbnail
) {
    public Novel mapBlueprintToNovel(Author author) {
        return Novel.builder()
                .title(title)
                .thumbnail(thumbnail)
                .synopsis(synopsis)
                .authorDescription(authorDescription)
                .hashtags(hashtag)
                .genre(genre)
                .ageRating(ageRating)
                .novelSize(novelSize)
                .hit(0)
                .score(0.0F)
                .participantCnt(0)
                .status(NovelStatus.IN_PROGRESS)
                .mainAuthor(author)
                .recentlyUpdated(LocalDateTime.now())
                .comments(new ArrayList<>())
                .chapters(new ArrayList<>())
                .authorities(new ArrayList<>())
                .novelLikes(new ArrayList<>())
                .build();
    }
}
