package us.usserver.domain.novel.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.novel.constant.Sorts;
import us.usserver.domain.novel.dto.NovelInfo;
import us.usserver.domain.novel.dto.req.MoreNovelReq;

import java.util.List;

@Builder
public record NovelPageInfoRes(
        @Schema(description = "더보기 분류에 따른 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> novelList,

        @Schema(description = "마지막 소설 ID", example = "5")
        MoreNovelReq lastNovelId,

        @Schema(description = "마지막 게시물 여부", example = "true")
        Boolean hasNext,

        @Schema(description = "소설 정렬", example = "NEW, DESC")
        Sorts sorts
) {}
