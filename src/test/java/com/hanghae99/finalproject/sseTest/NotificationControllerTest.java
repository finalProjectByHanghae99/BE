package com.hanghae99.finalproject.sseTest;

import com.hanghae99.finalproject.common.exception.CustomException;
import com.hanghae99.finalproject.common.exception.ErrorCode;
import com.hanghae99.finalproject.security.jwt.JwtTokenProvider;
import com.hanghae99.finalproject.sse.controller.NotificationController;
import com.hanghae99.finalproject.user.model.User;
import com.hanghae99.finalproject.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class NotificationControllerTest {
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    NotificationController notificationController;


    @Test
    @DisplayName("SSE에 연결을 진행한다.")
    public void subscribe() throws Exception {
        //given
        Long userId = 5L;
        User user = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER_INFO)
        );
        String accessToken = String.valueOf(jwtTokenProvider.createToken(user));

        //when, then
        mockMvc.perform(get("/subscribe")
                        .header("Authorization", accessToken))
                .andExpect(status().isOk());
    }

}
