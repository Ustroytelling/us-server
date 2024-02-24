package us.usserver.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GetMainPageResponse {
    @Schema(description = "실시간 업데이트 소설 List", example = "novel1, novel2, ...")
    private List<NovelInfo> realTimeUpdateNovels;

    @Schema(description = "신작 소설 List", example = "novel1, novel2, ...")
    private List<NovelInfo> newNovels;

    @Schema(description = "내가 읽은 소설 List", example = "novel1, novel2, ...")
    private List<NovelInfo> readNovels;
}
