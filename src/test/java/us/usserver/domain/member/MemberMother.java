package us.usserver.domain.member;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.EmailRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.domain.member.entity.Member;
import us.usserver.domain.member.constant.OauthProvider;
import us.usserver.domain.member.constant.Gender;
import us.usserver.domain.member.constant.Role;

import java.nio.charset.StandardCharsets;

import static org.jeasy.random.FieldPredicates.named;
import static org.jeasy.random.FieldPredicates.ofType;

public class MemberMother {
    public static Member generateMember() {
        EasyRandomParameters randomParameters = new EasyRandomParameters()
                .charset(StandardCharsets.UTF_8)
                .randomize(named("socialId").and(ofType(String.class)), new StringRandomizer(20))
                .randomize(named("email").and(ofType(String.class)), new EmailRandomizer(0))
                .randomize(named("age").and(ofType(Integer.class)), new IntegerRangeRandomizer(1, 100))
                .randomize(OauthProvider.class, () -> OauthProvider.KAKAO)
                .randomize(Role.class, () -> Role.USER)
                .randomize(Gender.class, () -> Gender.UNKNOWN);

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Member.class);
    }

}
