package us.usserver.domain.paragraph.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.paragraph.entity.Paragraph;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphSelected {
    @Schema(description = "한줄(paragraph) 식별 id", example = "234")
    private Long id;

    @Schema(description = "한줄 내용", example = "제가 LA에 있을 때... 주저리 주저리 궁시렁 궁시렁...")
    private String content;

    @Schema(description = "작성한 작가 식별 id", example = "123")
    private Long authorId;

    @Schema(description = "한 회차에서 몇 번째로 등장하는 한줄인지에 대한 인덱스", example = "1")
    private int sequence;

    @Schema(description = "사용자가 좋아요 한 한줄인지 확인", example = "true or false")
    private boolean isLiked;

    public static ParagraphSelected fromParagraph(Paragraph paragraph, boolean isLiked) {
        return ParagraphSelected.builder()
                .id(paragraph.getId())
                .content(paragraph.getContent())
                .authorId(paragraph.getAuthor().getId())
                .sequence(paragraph.getSequence())
                .isLiked(isLiked)
                .build();
    }
}
