package us.usserver.domain.author;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.domain.author.entity.Author;
import us.usserver.domain.member.entity.Member;

import java.nio.charset.StandardCharsets;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class AuthorMother {

    public static Author generateAuthor() {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("nickname").and(ofType(String.class)), new StringRandomizer(11))
                .randomize(named("introduction").and(ofType(String.class)), new StringRandomizer(100))
                .randomize(named("profileImg").and(ofType(String.class)), new StringRandomizer(300));

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Author.class);
    }

    public static Author generateAuthorWithMember(Member member) {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(Member.class, () -> member)
                .randomize(Long.class, member::getId)
                .randomize(named("nickname").and(ofType(String.class)), new StringRandomizer(11))
                .randomize(named("introduction").and(ofType(String.class)), new StringRandomizer(100))
                .randomize(named("profileImg").and(ofType(String.class)), new StringRandomizer(300));

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Author.class);
    }

}
