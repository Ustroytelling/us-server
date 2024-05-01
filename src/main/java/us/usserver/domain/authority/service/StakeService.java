package us.usserver.domain.authority.service;

import org.springframework.stereotype.Service;
import us.usserver.domain.novel.entity.Novel;
import us.usserver.domain.authority.dto.res.StakeInfoResponse;

@Service
public interface StakeService {
    // Q: 이런 경우에는 novelId를 받아서 set 메서드 내에서 Novel을 조회하는 것이 옳은가? 아니면 Novel을 받는 것이 옳은가?
    // A: Novel을 전달하는 것이 불필요한 쿼리를 생성 하지 않기 때문에 더 효율적이다.
    void setStakeInfoOfNovel(Novel novel);

    StakeInfoResponse getStakeInfoOfNovel(Long novelId);
}
