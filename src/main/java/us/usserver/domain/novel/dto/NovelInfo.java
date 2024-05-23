package us.usserver.domain.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import us.usserver.domain.author.dto.AuthorInfo;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.constant.Genre;
import us.usserver.domain.novel.constant.Hashtag;

import java.util.Set;

@Builder
public record NovelInfo(
        @Schema(description = "소설 ID", example = "1")
        Long id,
        @Schema(description = "소설 제목", example = "주술 회전")
        String title,
        @Schema(description = "소설 장르", example = "FANTASY")
        Genre genre,
        @Schema(description = "해시태그",example = "[HASHTAG1, HASHTAG2, ...]")
        Set<Hashtag> hashtag,
        @Schema(description = "메인 작가 이름",example = "강아지상")
        AuthorInfo createdAuthor,
        @Schema(description = "참여 작가 수",example = "12")
        Integer joinedAuthorCnt,
        @Schema(description = "댓글 갯수",example = "899")
        Integer commentCnt,
        @Schema(description = "좋아요 갯수",example = "998")
        Integer likeCnt,
        @Schema(description = "소설 공유 url",example = "https:// ~ ~")
        String novelSharelUrl,
        @Schema(description = "좋아요 유무", example = "true or false")
        Boolean isLiked
) {
    public static NovelInfo mapNovelToNovelInfo(Novel novel) {
        return NovelInfo.builder()
                .id(novel.getId())
                .title(novel.getTitle())
                .genre(novel.getGenre())
                .hashtag(novel.getHashtags())
                .createdAuthor(AuthorInfo.fromAuthor(novel.getMainAuthor()))
                .joinedAuthorCnt(novel.getParticipantCnt())
                .commentCnt(novel.getComments().size())
                .likeCnt(novel.getNovelLikes().size())
                .novelSharelUrl("")
                .build();
    }
}
