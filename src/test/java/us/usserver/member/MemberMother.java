package us.usserver.member;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.EmailRandomizer;
import org.jeasy.random.randomizers.range.IntegerRangeRandomizer;
import org.jeasy.random.randomizers.text.StringRandomizer;
import us.usserver.global.oauth.oauthEnum.SocialType;
import us.usserver.member.memberEnum.Gender;
import us.usserver.member.memberEnum.Role;

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
                .randomize(SocialType.class, () -> SocialType.KAKAO)
                .randomize(Role.class, () -> Role.USER)
                .randomize(Gender.class, () -> Gender.UNKNOWN);

        EasyRandom easyRandom = new EasyRandom(randomParameters);
        return easyRandom.nextObject(Member.class);
    }

}