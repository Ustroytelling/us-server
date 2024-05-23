package us.usserver.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.comment.entity.Comment;

import java.time.LocalDateTime;
import java.util.Objects;

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

    @Schema(description = "내가 좋아요를 눌렀는지 여부", example = "true or false")
    private Boolean iLiked;

    @Schema(description = "댓글 작성 날짜", example = "2023.12.31 00:00:00")
    private LocalDateTime createdAt;

    public static CommentInfo fromComment(Comment comment, String location, Integer likeCnt, Long memberId) {
        Author author = comment.getAuthor();
        return CommentInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(author.getNickname())
                .location(location)
                .likeCnt(likeCnt)
                .createdAt(comment.getCreatedAt())
                .iLiked(Objects.equals(author.getId(), memberId))
                .build();
    }

    public static CommentInfo mapCommentToCommentInfo(Comment comment, Long memberId) {
        String location;
        if (comment.getChapter() == null) {
            location = comment.getNovel().getTitle();
        } else {
            location = comment.getChapter().getTitle();
        }
        Author author = comment.getAuthor();
        return CommentInfo.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorName(author.getNickname())
                .location(location)
                .likeCnt(comment.getCommentLikes().size())
                .createdAt(comment.getCreatedAt())
                .iLiked(Objects.equals(author.getId(), memberId))
                .build();
    }
}
