package us.usserver.domain.paragraph.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.dto.AuthorInfo;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteInfo {
    @Schema(description = "투표 식별 id", example = "333")
    private Long Id;

    @Schema(description = "투표자 정보", example = "투표자 정보")
    private AuthorInfo author;

    @Schema(description = "투표중 한줄 정보", example = "투표 중인 한줄")
    private ParagraphInVoting paragraph;
}
