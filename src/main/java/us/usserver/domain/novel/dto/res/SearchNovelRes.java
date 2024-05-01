package us.usserver.domain.novel.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.novel.dto.NovelSimpleInfo;

import java.util.Set;

@Builder
public record SearchNovelRes(
        @Schema(description = "검색된 소설 list", example = "novel1, novel2, ...")
        Set<NovelSimpleInfo> novelSimpleInfos,
        @Schema(description = "다음 페이지 번호", example = "2")
        Integer nextPage,
        @Schema(description = "마지막 게시물 여부", example = "true")
        Boolean hasNext
) {}
