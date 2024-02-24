package us.usserver.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.novel.novelEnum.Orders;
import us.usserver.novel.novelEnum.Sorts;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SortDto {
    @Schema(description = "정렬 column", example = "HIT, NEW, LATEST")
    private Sorts sorts;

    @Schema(description = "정렬 type", example = "ASC, DESC")
    private Orders orders;
}
