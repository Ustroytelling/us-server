package us.usserver.domain.novel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
public record AuthorDescription(
        @Size(max = 300) @NotBlank
        String description
) {}
