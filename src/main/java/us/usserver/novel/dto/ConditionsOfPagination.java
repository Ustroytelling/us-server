package us.usserver.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConditionsOfPagination {
    @Schema(description = "마지막 소설 Id", nullable = true, example = "5")
    Long lastNovelId;

    @Schema(description = "더보기 개수", nullable = true, example = "6")
    Integer size;

    @Schema(description = "소설 정렬 및 필터", example = "HIT, DESC")
    @NotNull
    SortDto sortDto;
}
