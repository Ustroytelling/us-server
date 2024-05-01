package us.usserver.domain.author.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import us.usserver.domain.author.dto.res.GetParagraphNote;
import us.usserver.domain.author.service.NoteService;
import us.usserver.global.response.ApiCsResponse;

@Tag(name = "보관함(한줄) API")
@ResponseBody
@RestController
@RequestMapping("/notebook")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "내가 쓴 한줄 불러오기", description = "내가 작성한 모든 한줄 불러오기")
    @ApiResponse(responseCode = "200", description = "불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetParagraphNote.class)))
    @GetMapping("/wrote")
    public ApiCsResponse<GetParagraphNote> wroteParagraphs(@AuthenticationPrincipal Long memberId) {
        GetParagraphNote paragraphPreviews = noteService.wroteParagraphs(memberId);
        return ApiCsResponse.success(paragraphPreviews);
    }

    @Operation(summary = "내가 투표한 한줄 불러오기", description = "내가 투표한 모든 한줄 불러오기")
    @ApiResponse(responseCode = "200", description = "불러오기 성공",
            content = @Content(schema = @Schema(implementation = GetParagraphNote.class)))
    @GetMapping("/voted")
    public ApiCsResponse<GetParagraphNote> votedParagraphs(@AuthenticationPrincipal Long memberId) {
        GetParagraphNote paragraphPreviews = noteService.votedParagraphs(memberId);
        return ApiCsResponse.success(paragraphPreviews);
    }
    
    @Operation(summary = "내가 좋아요한 한줄 불러오기", description = "내가 좋아요한 모든 한줄 불러오기")
    @ApiResponse(responseCode = "200", description = "불러오기 성공",
                content = @Content(schema = @Schema(implementation = GetParagraphNote.class)))
    @GetMapping("/liked")
    public ApiCsResponse<GetParagraphNote> likedParagraphs(@AuthenticationPrincipal Long memberId) {
        GetParagraphNote paragraphPreviews = noteService.likedParagraphs(memberId);
        return ApiCsResponse.success(paragraphPreviews);
    }
}
