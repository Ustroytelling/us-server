package us.usserver.domain.paragraph.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.paragraph.dto.ParagraphInVoting;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetParagraphResponse {
    @Schema(description = "한줄 정보", example = "[한줄 정보 1, 한줄 정보 2, 한줄 정보 3, ...]")
    private List<ParagraphInVoting> paragraphInVotings;
}
