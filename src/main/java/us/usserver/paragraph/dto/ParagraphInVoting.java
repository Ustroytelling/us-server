package us.usserver.paragraph.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.paragraph.Paragraph;
import us.usserver.paragraph.paragraphEnum.ParagraphStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParagraphInVoting {
    private Long id;
    private String content;
    private int sequence;
    private int likeCnt;
    private ParagraphStatus status;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ParagraphInVoting fromParagraph(Paragraph paragraph, int likeCnt) {
        return ParagraphInVoting.builder()
                .id(paragraph.getId())
                .content(paragraph.getContent())
                .sequence(paragraph.getSequence())
                .likeCnt(likeCnt)
                .status(paragraph.getParagraphStatus())
                .authorId(paragraph.getAuthor().getId())
                .authorName(paragraph.getAuthor().getNickname())
                .build();
    }
}
