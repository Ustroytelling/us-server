package us.usserver.domain.authority.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.authority.entity.Authority;
import us.usserver.domain.authority.repository.AuthorityRepository;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.authority.dto.res.StakeInfoResponse;
import us.usserver.global.EntityFacade;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.paragraph.constant.ParagraphStatus;
import us.usserver.domain.authority.entity.Stake;
import us.usserver.domain.authority.repository.StakeRepository;
import us.usserver.domain.authority.dto.StakeInfo;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StakeServiceImpl implements StakeService {
    private final StakeRepository stakeRepository;
    private final AuthorityRepository authorityRepository;
    private final EntityFacade entityFacade;

    @Override
    @Transactional
    public void setStakeInfoOfNovel(Novel novel) {
        List<Chapter> chapters = novel.getChapters();
        float totalParagraphs = (float) getTotalParagraphs(chapters);

        List<Authority> authorities = authorityRepository.findAllByNovel(novel);
        Set<Author> authors = authorities.stream().map(Authority::getAuthor).collect(Collectors.toSet());

        for (Author author : authors) {
            float authorParagraphs = getAuthorParagraphs(chapters, author);
            updateStake(novel, author, authorParagraphs / totalParagraphs);
        }
    }

    @Override
    public StakeInfoResponse getStakeInfoOfNovel(Long novelId) {
        Novel novel = entityFacade.getNovel(novelId);

        List<Stake> stakes = stakeRepository.findAllByNovel(novel);
        List<StakeInfo> stakeInfos = stakes.stream().map(StakeInfo::fromStake).toList();

        return StakeInfoResponse.builder().stakeInfos(stakeInfos).build();
    }

    private int getTotalParagraphs(List<Chapter> chapters) {
        return chapters.stream()
                .mapToInt(chapter -> chapter.getParagraphs().size())
                .sum();
    }

    private float getAuthorParagraphs(List<Chapter> chapters, Author author) {
        return (float) chapters.stream()
                .flatMap(chapter -> chapter.getParagraphs().stream())
                .filter(paragraph -> paragraph.getAuthor().getId().equals(author.getId()))
                .filter(paragraph -> paragraph.getParagraphStatus().equals(ParagraphStatus.SELECTED))
                .count();
    }

    private void updateStake(Novel novel, Author author, Float percentage) {
        Optional<Stake> stakeByNovelAndAuthor = stakeRepository.findByNovelAndAuthor(novel, author);
        if (stakeByNovelAndAuthor.isEmpty()) {
            Stake stake = Stake.builder().novel(novel).author(author).percentage(percentage).build();
            stakeRepository.save(stake);
        } else {
            Stake stake = stakeByNovelAndAuthor.get();
            stake.setPercentage(percentage);
        }
    }
}

