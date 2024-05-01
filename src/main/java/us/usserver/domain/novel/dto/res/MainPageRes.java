package us.usserver.domain.novel.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.novel.dto.NovelInfo;

import java.util.List;

@Builder
public record MainPageRes(
        @Schema(description = "인기있는 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> popularNovels,

        @Schema(description = "내가 읽은 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> readNovels,

        @Schema(description = "실시간 업데이트 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> realTimeUpdateNovels,

        @Schema(description = "신작 소설 List", example = "novel1, novel2, ...")
        List<NovelInfo> recentlyCreatedNovels
) {}
