package us.usserver.domain.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.novel.entity.Novel;

@Builder
public record NovelPreview(
        @Schema(description = "조회된 소설 제목", example = "주술회전")
        String title,
        @Schema(description = "소설 메인 작가 이름", example = "냐옹씨")
        AuthorInfo mainAuthor,
        @Schema(description = "참여중인 작가들 수", example = "27")
        Integer joinedAuthor,
        @Schema(description = "소설 썸네일", example = "https://something-special-thumbnail.com/1")
        String thumbnail,
        @Schema(description = "바로가기", example = "https://some-url/novel/novelId")
        String shortcuts
) {
    public static NovelPreview fromNovel(Novel novel, Integer joinedAuthor, String shortcuts) {
        return NovelPreview.builder()
                .title(novel.getTitle())
                .mainAuthor(AuthorInfo.fromAuthor(novel.getMainAuthor()))
                .joinedAuthor(joinedAuthor)
                .thumbnail(novel.getThumbnail())
                .shortcuts(shortcuts)
                .build();
    }
}
