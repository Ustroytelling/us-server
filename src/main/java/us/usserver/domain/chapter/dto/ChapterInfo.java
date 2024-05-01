package us.usserver.domain.chapter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.chapter.entity.Chapter;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterInfo {
    private Long id;
    private String title;
    private Integer part;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ChapterStatus status;

    public static ChapterInfo fromChapter(Chapter chapter) {
        return ChapterInfo.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .part(chapter.getPart())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .status(chapter.getStatus())
                .build();
    }
}
