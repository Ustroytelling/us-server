package us.usserver.domain.chapter;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.chapter.dto.ChapterStatus;
import us.usserver.domain.novel.entity.Novel;

import java.nio.charset.StandardCharsets;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class ChapterMother {
    public static Chapter generateChapter(Novel novel) {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("title").and(ofType(String.class)), new StringRandomizer(100))
                .randomize(named("part").and(ofType(Integer.class)), new IntegerRangeRandomizer(0, 100))
                .randomize(ChapterStatus.class, () -> ChapterStatus.IN_PROGRESS)
                .randomize(Novel.class, () -> novel);


        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Chapter.class);
    }
}
