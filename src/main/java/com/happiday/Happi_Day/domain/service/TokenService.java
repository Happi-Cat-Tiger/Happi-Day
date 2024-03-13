package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.CustomUserDetails;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.jwt.JwtTokenResponse;
import com.happiday.Happi_Day.jwt.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private String key = "refresh";

    public JwtTokenResponse setToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        String accessToken = jwtTokenUtils.createAccessToken(CustomUserDetails.fromEntity(user));
        String refreshToken = jwtTokenUtils.createRefreshToken(CustomUserDetails.fromEntity(user));
        redisTemplate.opsForValue().set(username, refreshToken, Duration.ofMinutes(2));
        JwtTokenResponse token = new JwtTokenResponse(accessToken, refreshToken);
        return token;
    }

    public void logout(String username) {
        redisTemplate.delete(username);
    }
}
