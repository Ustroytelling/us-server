package us.usserver.domain.novel.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Rollback
@Transactional
@SpringBootTest
class KomoranServiceTest {
    @Autowired
    private KomoranService komoranService;

    @Test
    @DisplayName("명사, 영어 분리 TEST")
    void tokenizeKeyword() {
        // given
        String keyword = "소설계의 GOAT 광마회귀";

        // when
        Set<String> tokens = komoranService.tokenizeKeyword(keyword);

        // then
        tokens.forEach(token -> Assertions.assertThat("소설 goat 회귀").contains(token));
    }
}