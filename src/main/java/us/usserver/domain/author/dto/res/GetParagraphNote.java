package us.usserver.domain.author.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.dto.ParagraphPreview;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetParagraphNote {
    @Schema(description = "보관함 내 한줄 보기", example = "[한줄 미리보기1, 한줄 미리보기2, 한줄 미리보기3, ...]")
    private List<ParagraphPreview> paragraphPreviews;
}
