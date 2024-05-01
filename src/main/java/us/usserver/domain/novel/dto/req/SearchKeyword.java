package us.usserver.domain.novel.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import us.usserver.domain.novel.constant.Hashtag;
import us.usserver.domain.novel.constant.NovelStatus;

@Builder
public record SearchKeyword(
        @NotEmpty @Schema(description = "검색어", example = "주술")
        String keyword,

        @Min(0)
        @Schema(description = "다음 소설 페이지", nullable = true, example = "더보기 request의 response에 보면 page가 적혀 있음 그 값 전달", defaultValue = "0")
        Integer nextPage
) {}