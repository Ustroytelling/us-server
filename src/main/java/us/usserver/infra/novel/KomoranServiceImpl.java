package us.usserver.infra.novel;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.util.common.model.Pair;
import org.springframework.stereotype.Service;
import us.usserver.domain.novel.service.KomoranService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KomoranServiceImpl implements KomoranService {
    public Set<String> tokenizeKeyword(String keyword) {
        Komoran komoran = new Komoran(DEFAULT_MODEL.LIGHT);
        KomoranResult komoranResult = komoran.analyze(keyword);
        List<Pair<String, String>> list = komoranResult.getList();

        Set<String> keywords = list.stream()
                .filter(stringStringPair -> stringStringPair.getSecond().contentEquals("NNG")) // 명사
                .map(Pair::getFirst)
                .collect(Collectors.toSet());

        keywords.addAll(list.stream()
                .filter(stringStringPair -> stringStringPair.getSecond().contentEquals("SL")) // 영어
                .map(Pair::getFirst)
                .map(String::toLowerCase)
                .toList());

        return keywords;
    }
}
