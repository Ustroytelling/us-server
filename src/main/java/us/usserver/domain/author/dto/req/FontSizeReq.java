package us.usserver.domain.author.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record FontSizeReq(
        @Min(1) @Max(30)
        @Schema(description = "글자 크기", example = "16")
        Integer fontSize
) {}
