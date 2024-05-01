package us.usserver.domain.novel.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.novel.dto.NovelInfo;

import java.util.List;

@Builder
public record MoreNovelRes(
        @Schema(description = "더보기 분류에 따른 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> novelList,
        @Schema(description = "다음 페이지 번호", example = "2")
        Integer nextPage,
        @Schema(description = "마지막 게시물 여부", example = "true")
        Boolean hasNext
) {}
