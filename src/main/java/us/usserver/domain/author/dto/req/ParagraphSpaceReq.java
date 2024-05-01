package us.usserver.domain.author.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public record ParagraphSpaceReq(
        @Min(1) @Max(30)
        @Schema(description = "단락 간격", example = "16")
        Integer paragraphSpace
) {}
