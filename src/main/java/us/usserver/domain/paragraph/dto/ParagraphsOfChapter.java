package us.usserver.domain.paragraph.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphsOfChapter {
    @Schema(description = "선정 된 한줄 들", example = "[설정된 한줄 정보1, 설정된 한줄 정보2, ...]")
    private List<ParagraphSelected> selectedParagraphs;

    @Schema(description = "내가 쓴 한줄", example = "투표 중인 한줄")
    private ParagraphInVoting myParagraph;
    
    @Schema(description = "가장 투표를 많이 받은 한줄", example = "투표 중인 한줄")
    private ParagraphInVoting bestParagraph;
}
