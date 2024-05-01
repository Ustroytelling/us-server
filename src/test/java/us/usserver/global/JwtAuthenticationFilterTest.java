package us.usserver.global;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import us.usserver.global.response.exception.BaseException;
import us.usserver.global.response.exception.ExceptionMessage;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class JwtAuthenticationFilterTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("JWT Filter 작동 API TETS")
    void getCommentsOfNovel1() {
        // given

        // when then
        BaseException baseException = Assertions.assertThrows(BaseException.class,
                () -> mockMvc.perform(MockMvcRequestBuilders.get("").contentType(MediaType.APPLICATION_JSON)));

        // then
        Assertions.assertTrue(baseException.getMessage().contains(ExceptionMessage.TOKEN_NOT_FOUND));
    }

}
