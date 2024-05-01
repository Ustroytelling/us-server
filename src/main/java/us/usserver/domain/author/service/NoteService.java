package us.usserver.domain.author.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.author.dto.res.GetParagraphNote;

@Service
public interface NoteService {
    GetParagraphNote wroteParagraphs(Long memberId);

    GetParagraphNote votedParagraphs(Long memberId);

    GetParagraphNote likedParagraphs(Long memberId);
}
