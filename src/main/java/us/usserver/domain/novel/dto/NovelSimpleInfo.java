package us.usserver.domain.novel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import us.usserver.domain.author.dto.AuthorInfo;
import us.usserver.domain.novel.entity.Novel;

@Builder
public record NovelSimpleInfo(
        @Schema(description = "소설 ID", example = "1")
        Long id,
        @Schema(description = "소설 제목", example = "주술 회전")
        String title,
        @Schema(description = "메인 작가 이름",example = "강아지상")
        String createdAuthor,
        @Schema(description = "참여 작가 수",example = "12")
        Integer joinedAuthorCnt,
        @Schema(description = "댓글 갯수",example = "899")
        Integer commentCnt,
        @Schema(description = "평점",example = "9.8")
        Float score
) {
        public static NovelSimpleInfo mapNovelToSimpleInfo(Novel novel) {
                return NovelSimpleInfo.builder()
                        .id(novel.getId())
                        .title(novel.getTitle())
                        .createdAuthor(novel.getMainAuthor().getNickname())
                        .joinedAuthorCnt(novel.getParticipantCnt())
                        .commentCnt(novel.getComments().size())
                        .score(novel.getScore())
                        .build();
        }
}
