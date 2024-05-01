package us.usserver.domain.author.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.paragraph.repository.ParagraphLikeRepository;
import us.usserver.domain.author.dto.res.GetParagraphNote;
import us.usserver.domain.author.dto.ParagraphPreview;
import us.usserver.domain.paragraph.repository.VoteRepository;
import us.usserver.global.EntityFacade;
import us.usserver.domain.paragraph.entity.ParagraphLike;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.repository.ParagraphRepository;
import us.usserver.domain.paragraph.entity.Vote;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final EntityFacade entityFacade;

    private final ParagraphRepository paragraphRepository;
    private final VoteRepository voteRepository;
    private final ParagraphLikeRepository paragraphLikeRepository;

    @Override
    public GetParagraphNote wroteParagraphs(Long memberId) {
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();

        List<Paragraph> paragraphs = paragraphRepository.findAllByAuthor(author);
        List<ParagraphPreview> paragraphPreviews = paragraphs.stream().map(paragraph -> ParagraphPreview.fromParagraph(
                paragraph,
                paragraph.getChapter().getNovel(),
                paragraph.getChapter()
        )).toList();

        return GetParagraphNote.builder().paragraphPreviews(paragraphPreviews).build();
    }

    @Override
    public GetParagraphNote votedParagraphs(Long memberId) {
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();

        List<Vote> votes = voteRepository.findAllByAuthor(author);
        List<ParagraphPreview> paragraphPreviews = votes.stream().map(vote -> ParagraphPreview.fromParagraph(
                vote.getParagraph(),
                vote.getParagraph().getChapter().getNovel(),
                vote.getParagraph().getChapter()
        )).toList();

        return GetParagraphNote.builder().paragraphPreviews(paragraphPreviews).build();
    }

    @Override
    public GetParagraphNote likedParagraphs(Long memberId) {
        Member member = entityFacade.getMember(memberId);
        Author author = member.getAuthor();

        List<ParagraphLike> paragraphLikes = paragraphLikeRepository.findAllByAuthor(author);
        List<ParagraphPreview> paragraphPreviews = paragraphLikes.stream().map(paragraphLike -> ParagraphPreview.fromParagraph(
                paragraphLike.getParagraph(),
                paragraphLike.getParagraph().getChapter().getNovel(),
                paragraphLike.getParagraph().getChapter()
        )).toList();

        return GetParagraphNote.builder().paragraphPreviews(paragraphPreviews).build();
    }
}
