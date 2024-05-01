package us.usserver.domain.author.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.author.dto.NovelPreview;

import java.util.List;

@Builder
public record BookshelfDefaultResponse(
        @Schema(description = "더보기 분류에 따른 소설 List", example = "소설미리보기1, 소설미리보기2, 소설미리보기3, ...")
        List<NovelPreview> novelPreviews,
        @Schema(description = "소설 총 갯수", example = "13")
        Integer count
) {}
