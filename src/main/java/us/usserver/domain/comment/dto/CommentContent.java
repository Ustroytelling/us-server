package us.usserver.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record CommentContent(
        @Length(max = 300)
        @Schema(description = "댓글 내용", example = "이것이 댓글이다.")
        String content
) {}
