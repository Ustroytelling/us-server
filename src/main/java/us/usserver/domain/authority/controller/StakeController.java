package us.usserver.domain.authority.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.authority.dto.res.StakeInfoResponse;
import us.usserver.domain.authority.service.StakeService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "지분 API")
@ResponseBody
@RestController
@RequestMapping("/stake")
@RequiredArgsConstructor
public class StakeController {
    private final StakeService stakeService;

    @Operation(summary = "지분 정보 조회", description = "특정 소설에 대한 지분 정보 확인")
    @ApiResponse(responseCode = "200", description = "작성 성공",
            content = @Content(schema = @Schema(implementation = StakeInfoResponse.class)))
    @GetMapping("/{novelId}")
    public ApiCsResponse<StakeInfoResponse> getStakes(@PathVariable Long novelId) {
        StakeInfoResponse stakeInfos = stakeService.getStakeInfoOfNovel(novelId);
        return ApiCsResponse.success(stakeInfos);
    }
}
