package us.usserver.comment.novel;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import us.usserver.global.ApiCsResponse;
import us.usserver.comment.novel.dto.CommentsInNovelRes;
import us.usserver.comment.novel.dto.PostCommentReq;

import java.util.List;

@ResponseBody
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class NoCommentController {
    private final NoCommentService noCommentService;

    @GetMapping("/{novelId}")
    public ResponseEntity<ApiCsResponse<?>> getCommentsInNovel(@PathVariable Long novelId) {
        List<CommentsInNovelRes> commentsInNovel = noCommentService.getCommentsInNovel(novelId);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(commentsInNovel)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{novelId}")
    public ResponseEntity<ApiCsResponse<?>> writeCommentsInNovel(
            @PathVariable Long novelId,
            @Validated @RequestBody PostCommentReq postCommentReq
    ) {
        Long authorId = 0L; // TODO: 토큰 에서 뺴올 예정
        List<CommentsInNovelRes> commentsInNovel = noCommentService
                .postCommentInNovel(novelId, authorId, postCommentReq);

        ApiCsResponse<Object> response = ApiCsResponse.builder()
                .status(HttpStatus.CREATED.value())
                .message(HttpStatus.CREATED.getReasonPhrase())
                .data(commentsInNovel)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
