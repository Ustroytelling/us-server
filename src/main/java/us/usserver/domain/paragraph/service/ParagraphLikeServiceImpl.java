package us.usserver.domain.paragraph.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.entity.ParagraphLike;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.global.EntityFacade;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ErrorCode;
import us.usserver.global.response.exception.ExceptionMessage;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ParagraphLikeServiceImpl implements ParagraphLikeService {
    private final EntityFacade entityFacade;
    private final ParagraphLikeRepository paragraphLikeRepository;

    @Override
    public void setParagraphLike(Long paragraphId, Long memberId) {
        Paragraph paragraph = entityFacade.getParagraph(paragraphId);
        Author author = entityFacade.getAuthorByMemberId(memberId);

        paragraphLikeRepository.findByParagraphAndAuthor(paragraph, author)
                .ifPresent(paragraphLike -> {
                    throw new BaseException(ErrorCode.LIKE_DUPLICATED);});
        if (paragraph.getParagraphStatus() != ParagraphStatus.SELECTED) {
            throw new UnsupportedOperationException(ExceptionMessage.LIKE_ONLY_SELECTED_PARAGRAPH);
        }

        ParagraphLike paragraphLike = ParagraphLike.builder()
                .paragraph(paragraph).author(author).build();
        paragraph.addParagraphLike(paragraphLike);
        paragraphLikeRepository.save(paragraphLike);
    }

    @Override
    public void deleteParagraphLike(Long paragraphId, Long memberId) {
        Paragraph paragraph = entityFacade.getParagraph(paragraphId);
        Optional<ParagraphLike> byParagraphIdAndAuthorId = paragraphLikeRepository.findByParagraphIdAndAuthorId(paragraphId, memberId);

        if (byParagraphIdAndAuthorId.isPresent()) {
            ParagraphLike paragraphLike = byParagraphIdAndAuthorId.get();

            paragraph.removeParagraphLike(paragraphLike);
            paragraphLikeRepository.delete(paragraphLike);
        } else {
            throw new BaseException(ErrorCode.PARAGRAPH_LIKE_NOT_FOUND);
        }
    }
}
