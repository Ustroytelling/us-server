package us.usserver.domain.authority.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import us.usserver.domain.authority.dto.StakeInfo;

import java.util.List;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StakeInfoResponse {
    @Schema(description = "사용자 정보", example = "[지분 정보1, 지분 정보2, 지분 정보3, ...]")
    private List<StakeInfo> stakeInfos;
}
