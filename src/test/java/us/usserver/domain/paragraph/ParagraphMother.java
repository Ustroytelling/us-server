package us.usserver.domain.paragraph;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.chapter.entity.Chapter;
import us.usserver.domain.paragraph.entity.Paragraph;
import us.usserver.domain.paragraph.constant.ParagraphStatus;

import java.nio.charset.StandardCharsets;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class ParagraphMother {
    public static Paragraph generateParagraph(Author author, Chapter chapter) {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("content").and(ofType(String.class)), new StringRandomizer(50, 300, 0))
                .randomize(named("sequence").and(ofType(int.class)), new IntegerRangeRandomizer(0, 15))
                .randomize(ParagraphStatus.class, () -> ParagraphStatus.IN_VOTING)
                .randomize(Author.class, () -> author)
                .randomize(Chapter.class, () -> chapter);

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Paragraph.class);
    }

    public static Paragraph generateParagraphWithSeed(Author author, Chapter chapter, int seed) {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("content").and(ofType(String.class)), new StringRandomizer(50, 300, seed))
                .randomize(named("sequence").and(ofType(int.class)), new IntegerRangeRandomizer(0, 15))
                .randomize(ParagraphStatus.class, () -> ParagraphStatus.IN_VOTING)
                .randomize(Author.class, () -> author)
                .randomize(Chapter.class, () -> chapter);

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Paragraph.class);
    }
}
