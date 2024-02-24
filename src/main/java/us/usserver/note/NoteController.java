package us.usserver.note;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import us.usserver.bookshelf.dto.NovelPreview;
import us.usserver.global.ApiCsResponse;
import us.usserver.global.exception.AuthorNotFoundException;
import us.usserver.global.exception.DuplicatedLikeException;
import us.usserver.global.exception.ParagraphNotFoundException;
import us.usserver.note.dto.GetParagraphNote;
import us.usserver.note.dto.ParagraphPreview;

import java.util.List;

@Tag(name = "보관함(한줄) API")
@ResponseBody
@RestController
@RequestMapping("/notebook")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "내가 쓴 한줄 불러오기", description = "내가 작성한 모든 한줄 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "불러오기 성공",
                    content = @Content(schema = @Schema(implementation = GetParagraphNote.class))),
            @ApiResponse(
                    responseCode = "400", description = "작가 정보가 유효하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @GetMapping("/viewed") // TODO: 내가 쓴 글
    public ResponseEntity<ApiCsResponse<?>> wroteParagraphs() {
        Long authorId = 500L;
        GetParagraphNote paragraphPreviews = noteService.wroteParagraphs(authorId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(paragraphPreviews)
                .build();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 투표한 한줄 불러오기", description = "내가 투표한 모든 한줄 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "불러오기 성공",
                    content = @Content(schema = @Schema(implementation = GetParagraphNote.class))),
            @ApiResponse(
                    responseCode = "400", description = "작가 정보가 유효하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @GetMapping("/voted") // TODO: 내가 투표한 글
    public ResponseEntity<ApiCsResponse<?>> votedParagraphs() {
        Long authorId = 500L;
        GetParagraphNote paragraphPreviews = noteService.votedParagraphs(authorId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(paragraphPreviews)
                .build();
        return ResponseEntity.ok(response);
    }
    
    @Operation(summary = "내가 좋아요한 한줄 불러오기", description = "내가 좋아요한 모든 한줄 불러오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "불러오기 성공",
                    content = @Content(schema = @Schema(implementation = GetParagraphNote.class))),
            @ApiResponse(
                    responseCode = "400", description = "작가 정보가 유효하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = AuthorNotFoundException.class)))
    })
    @GetMapping("/liked") // TODO: 내가 좋아요한 글
    public ResponseEntity<ApiCsResponse<?>> likedNovels() {
        Long authorId = 500L;
        GetParagraphNote paragraphPreviews = noteService.likedParagraphs(authorId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(paragraphPreviews)
                .build();
        return ResponseEntity.ok(response);
    }
}
