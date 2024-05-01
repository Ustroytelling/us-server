package us.usserver.domain.novel.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import us.usserver.domain.novel.dto.MainNovelType;

@Builder
public record MoreNovelReq(
        @NotNull @Min(0)
        @Schema(description = "다음 소설 페이지", nullable = true, example = "더보기 request의 response에 보면 page가 적혀 있음 그 값 전달", defaultValue = "0")
        Integer nextPage,

        @NotNull
        @Schema(description = "더보기 탭 종류", nullable = true, example = "실시간 업데이트 or 인기 소설 or 따끈따끈한 신작 or 읽은 소설")
        MainNovelType mainNovelType
) {}
