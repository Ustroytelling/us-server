package us.usserver.author;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.author.dto.FontSizeReq;
import us.usserver.author.dto.ParagraphSpaceReq;
import us.usserver.author.dto.UpdateAuthorReq;
import us.usserver.global.ApiCsResponse;
import us.usserver.global.exception.AuthorNotFoundException;
import us.usserver.global.exception.FontSizeOutOfRangeException;
import us.usserver.member.Member;

@Tag(name = "사용자 정보 API")
@RequiredArgsConstructor
@RequestMapping("/author")
@RestController
public class AuthorController {
    private final AuthorService authorService;

    @Operation(summary = "사용자 정보 수정", description = "사용자 정보를 수정하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "작가가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @PatchMapping
    public ResponseEntity<ApiCsResponse<?>> updateAuthor(@AuthenticationPrincipal Member member,
                                                         @ModelAttribute UpdateAuthorReq updateAuthorReq) {
        authorService.updateAuthor(member.getId(), updateAuthorReq);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "글씨 크기 수정", description = "글씨 크기 조정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글씨 크기 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "1 ~ 30 범위.",
                    content = @Content(schema = @Schema(implementation = FontSizeOutOfRangeException.class)))
    })
    @PatchMapping("/fontsize")
    public ResponseEntity<ApiCsResponse<?>> changeFontSize(@Validated @RequestBody FontSizeReq req) {
        Long authorId = 500L;
        authorService.changeFontSize(authorId, req.getFontSize());

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "단란 간격 수정", description = "단락 간격 조정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "글씨 크기 업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "1 ~ 30 범위.",
                    content = @Content(schema = @Schema(implementation = FontSizeOutOfRangeException.class)))
    })
    @PatchMapping("/paragraph-space")
    public ResponseEntity<ApiCsResponse<?>> changeParagraphSpace(@Validated @RequestBody ParagraphSpaceReq req) {
        Long authorId = 500L;
        authorService.changeParagraphSpace(authorId, req.getParagraphSpace());

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}