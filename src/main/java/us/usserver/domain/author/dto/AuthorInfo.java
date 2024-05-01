package us.usserver.domain.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.author.entity.Author;

@Builder
public record AuthorInfo(
        @Schema(description = "작가 식별 id", example = "333")
        Long id,
        @Schema(description = "작가 닉네임", example = "특별한 닉네임")
        String nickname
) {
    public static AuthorInfo fromAuthor(Author author) {
        return AuthorInfo.builder()
                .id(author.getId())
                .nickname(author.getNickname())
                .build();
    }
}
