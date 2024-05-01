package us.usserver.domain.paragraph.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import us.usserver.domain.paragraph.service.VoteService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "투표 API")
@ResponseBody
@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @Operation(summary = "투표 하기", description = "특정 한줄에 투표하기")
    @ApiResponse(responseCode = "204", description = "투표 성공")
    @PostMapping("/{paragraphId}")
    public ApiCsResponse<Void> voting(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        voteService.voting(memberId, paragraphId);
        return ApiCsResponse.success();
    }

    @Operation(summary = "투표 취소 하기", description = "본인 투표 취소 하기")
    @ApiResponse(responseCode = "204", description = "취소 성공")
    @DeleteMapping("/{paragraphId}")
    public ApiCsResponse<Void> cancelVote(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long paragraphId
    ) {
        voteService.unvoting(memberId, paragraphId);
        return ApiCsResponse.success();
    }
}
