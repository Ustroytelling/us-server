package us.usserver.domain.authority.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import us.usserver.domain.author.dto.AuthorInfo;
import us.usserver.domain.authority.entity.Stake;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StakeInfo {
    @Schema(description = "사용자 정보")
    private AuthorInfo authorInfo;

    @Schema(description = "지분 퍼센트", example = "13.5")
    private Float percentage;

    public static StakeInfo fromStake(Stake stake) {
        return StakeInfo.builder()
                .authorInfo(AuthorInfo.fromAuthor(stake.getAuthor()))
                .percentage(stake.getPercentage())
                .build();
    }
}
