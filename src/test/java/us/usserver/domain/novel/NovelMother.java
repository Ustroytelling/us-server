package us.usserver.domain.novel;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.misc.EnumRandomizer;
import org.jeasy.random.randomizers.range.FloatRangeRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.novel.dto.AuthorDescription;
import us.usserver.domain.novel.dto.req.NovelSynopsis;
import us.usserver.domain.novel.constant.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class NovelMother {
    public static Novel generateNovel(Author author) {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("title").and(ofType(String.class)), new StringRandomizer(16))
                .randomize(named("thumbnail").and(ofType(String.class)), new StringRandomizer(500)) // URL이 들어갈 예정이라 대충 잡아 놓음
                .randomize(named("synopsis").and(ofType(String.class)), new StringRandomizer(300))
                .randomize(named("authorDescription").and(ofType(String.class)), new StringRandomizer(300))
                .randomize(named("hashtag").and(ofType(Set.class)), () -> EnumSet.of(Hashtag.판타지))
                .randomize(Genre.class, new EnumRandomizer<>(Genre.class))
                .randomize(AgeRating.class, new EnumRandomizer<>(AgeRating.class))
                .randomize(NovelStatus.class, new EnumRandomizer<>(NovelStatus.class))
                .randomize(named("hit").and(ofType(Integer.class)), new IntegerRangeRandomizer(0, 0))
                .randomize(named("score").and(ofType(Float.class)), new FloatRangeRandomizer(0.0F, 10.0F))
                .randomize(named("participantCnt").and(ofType(Integer.class)), new IntegerRangeRandomizer(0, 0))
                .randomize(NovelSize.class, new EnumRandomizer<>(NovelSize.class))
                .randomize(Author.class, () -> author)
                .randomize(named("recentlyUpdated"), LocalDateTime::now);

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Novel.class);
    }

    public static NovelSynopsis generateSysnopsis() {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(String.class, new StringRandomizer(300));

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(NovelSynopsis.class);
    }

    public static AuthorDescription generateDescription() {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(String.class, new StringRandomizer(300));

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(AuthorDescription.class);
    }
}
