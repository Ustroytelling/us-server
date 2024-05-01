package us.usserver.domain.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.paragraph.entity.Paragraph;

import java.time.LocalDateTime;

@Builder
public record ParagraphPreview(
        @Schema(description = "작성한 작가 식별 id", example = "12")
        Long authorId,
        @Schema(description = "소설 제목", example = "주술회전")
        String novelTitle,
        @Schema(description = "회차 제목", example = "주술회전 2화")
        String chapterTitle,
        @Schema(description = "한줄 내용", example = "제가 LA에 있을때... 주저리 주저리")
        String paragraphContent,
        @Schema(description = "소설 썸네일", example = "https://thumbnail-url.com/2")
        String thumbnail,
        @Schema(description = "메인 바로가기", example = "https://someurl.com/novel/2")
        String shortcuts,
        @Schema(description = "한줄 작성 날짜", example = "2023.12.31 00:00:00")
        LocalDateTime date
) {
    @Value("${aws.public.ip}")
    private static String publicIp;

    public static ParagraphPreview fromParagraph(Paragraph paragraph, Novel novel, Chapter chapter) {
        return ParagraphPreview.builder()
                .authorId(paragraph.getAuthor().getId())
                .novelTitle(novel.getTitle())
                .chapterTitle(chapter.getTitle())
                .paragraphContent(paragraph.getContent())
                .thumbnail(novel.getThumbnail())
                .shortcuts(getShortcuts(novel.getId(), chapter.getId()))
                .date(paragraph.getCreatedAt())
                .build();
    }

    private static String getShortcuts(Long novelId, Long chapterId) {
        return "http://" + publicIp + ":8080/" + novelId + "/" + chapterId;
    }
}
