package us.usserver.domain.paragraph.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record PostParagraphReq(
        @Length(min = 50, max = 300)
        @Schema(description = "한줄 내용", example = "제가 1994년에 LA에 있었을 때... 이러쿵 저러쿵")
        String content
) {}
