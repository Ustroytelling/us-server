package us.usserver.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.comment.entity.Comment;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentInfo {
    @Schema(description = "댓글 식별 id", example = "12")
    private Long id;
    
    @Schema(description = "댓글 내용", example = "이것이 댓글이다.")
    private String content;

    @Schema(description = "댓글 작성자 이름", example = "고야아앙이")
    private String authorName;

    @Schema(description = "댓글 위치", example = "소설 제목, 회차 제목")
    private String location;

    @Schema(description = "댓글 좋아요 갯수", example = "3")
    private Integer likeCnt;

    @Schema(description = "댓글 작성 날짜", example = "2023.12.31 00:00:00")
    private LocalDateTime createdAt;

    public static CommentInfo fromComment(Comment comment, String location, Integer likeCnt) {
        return CommentInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getNickname())
                .location(location)
                .likeCnt(likeCnt)
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static CommentInfo mapCommentToCommentInfo(Comment comment) {
        String location;
        if (comment.getChapter() == null) {
            location = comment.getNovel().getTitle();
        } else {
            location = comment.getChapter().getTitle();
        }
        return CommentInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getAuthor().getNickname())
                .location(location)
                .likeCnt(comment.getCommentLikes().size())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
