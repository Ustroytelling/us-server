package us.usserver.domain.chapter.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostScore {
    @Schema(description = "회차 평점", example = "1 ~ 10")
    @Min(1)
    @Max(10)
    @NotNull
    private Integer score;
}
