package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.jwt.JwtTokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private User testUser;

    @BeforeEach
    public void init() {
        testUser = User.builder()
                .username("test@email.com")
                .password("qwer1234")
                .nickname("닉네임")
                .realname("테스트")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {;
        redisTemplate.getConnectionFactory().getConnection().flushAll();
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