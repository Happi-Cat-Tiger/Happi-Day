package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.jwt.JwtTokenUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class TokenServiceTest {

    @Mock
    UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @Mock
    private StringRedisTemplate redisTemplate;

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("토큰 저장 확인")
    @Transactional
    void setToken() {

        // given
        String username = "test@email.com";

        String accessToken = "mockedAccessToken";
        String refreshToken = "mockedRefreshToken";

        User mockedUser = User.builder()
                .username("test@email.com")
                .password("qwer1234")
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .build();
        userRepository.save(mockedUser);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockedUser));
        Mockito.when(jwtTokenUtils.createAccessToken(Mockito.any())).thenReturn(accessToken);
        Mockito.when(jwtTokenUtils.createRefreshToken(Mockito.any())).thenReturn(refreshToken);

        // when
        String result = tokenService.setToken(username);

        // then
        Mockito.verify(redisTemplate.opsForValue(), Mockito.times(1))
                .set(username, refreshToken, Duration.ofMinutes(2));
        Assertions.assertEquals(accessToken, result);
        Assertions.assertNotNull(result);


//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("test@email.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("테스트");
//        dto.setRealname("김철수");
//        dto.setPhone("01012341234");
//        userService.createUser(dto);
//
//        String accessToken = "mockedAccessToken";
//        String refreshToken = "mockedRefreshToken";
//        Mockito.when(jwtTokenUtils.createAccessToken(Mockito.any())).thenReturn(accessToken);
//        Mockito.when(jwtTokenUtils.createRefreshToken(Mockito.any())).thenReturn(refreshToken);
//
//        ValueOperations<String, String> valueOperationsMock = Mockito.mock(ValueOperations.class);
//
//        // RedisTemplate을 Mock으로 대체
//        Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperationsMock);
//        Mockito.doNothing().when(valueOperationsMock).set(Mockito.anyString(), Mockito.anyString(), Mockito.any());
//
//        // when
//        String result = tokenService.setToken("test@email.com");
//
//        // then
//        Mockito.verify(redisTemplate.opsForValue(), Mockito.times(1)).set("test@email.com", refreshToken, Duration.ofMinutes(2));
//        Assertions.assertEquals(accessToken, result);
//        Assertions.assertNotNull(result);
    }

    @Test
    void logout() {
    }
}