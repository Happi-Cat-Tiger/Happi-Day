package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.dto.UserRegisterDto;
import com.happiday.Happi_Day.jwt.JwtTokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @BeforeEach
    public void init() {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("test@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("테스트");
        dto.setRealname("김철수");
        dto.setPhone("01012341234");
        userService.createUser(dto);
    }

    @Test
    void RefreshToken_저장_성공() {
        // given
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();

        // when
        JwtTokenResponse response = tokenService.setToken("test@email.com");

        // then
        String savedCode = stringStringValueOperations.get("test@email.com");

        Assertions.assertThat(response.getAccessToken()).isNotNull();
        Assertions.assertThat(response.getRefreshToken()).isEqualTo(savedCode);
    }

    @Test
    void 로그아웃_성공시_redis_삭제() {
        // given
        ValueOperations<String, String> stringStringValueOperations = redisTemplate.opsForValue();
        JwtTokenResponse response = tokenService.setToken("test@email.com");

        // when
        tokenService.logout("test@email.com");

        // then
        Assertions.assertThat(stringStringValueOperations.get("test@email.com")).isNull();
    }
}