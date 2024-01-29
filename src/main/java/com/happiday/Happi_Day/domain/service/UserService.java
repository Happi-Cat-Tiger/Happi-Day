package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.*;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final StringRedisTemplate stringRedisTemplate;
    private final FileUtils fileUtils;
    private final DefaultImageUtils defaultImageUtils;

    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto updateUserProfile(String username, UserUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (userRepository.existsByNickname(dto.getNickname())) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }
        if (userRepository.existsByPhone(dto.getPhone())) {
            throw new CustomException(ErrorCode.PHONE_CONFLICT);
        }
        else
            user.update(dto.toEntity(), passwordEncoder);

        userRepository.save(user);
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto changeImage(String username, MultipartFile multipartFile) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (multipartFile != null && !multipartFile.isEmpty()) {
            String url = fileUtils.uploadFile(multipartFile);
            user.setImageUrl(url);
        }
        userRepository.save(user);
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto resetImage(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setImageUrl(defaultImageUtils.getDefaultImageUrlUserProfile());
        userRepository.save(user);
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public void deleteUser(String username, UserPWDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);
        }
        userRepository.delete(user);
    }

    public String findPassword(UserFindDto dto) throws Exception {
        // 이름 + 이메일 입력
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!user.getRealname().equals(dto.getRealname())) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        String code = mailService.createNumber();
        mailService.sendEmail(dto.getUsername(), code);

        String key = "code:" + dto.getUsername();
        stringRedisTemplate.opsForValue().set(key, code);
        stringRedisTemplate.expire(key, 600, TimeUnit.SECONDS);

        return code;
    }

    public Boolean checkEmail(UserNumDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String key = "code:" + dto.getUsername();
        String storedCode = stringRedisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            throw new CustomException(ErrorCode.CODE_TIME_ERROR);
        } else if (!dto.getCode().equals(storedCode)) {
            throw new CustomException(ErrorCode.CODE_NOT_MATCHED);
        }
        return true;
    }

    @Transactional
    public void changePassword(UserLoginDto dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.setPassword(dto.getPassword(), passwordEncoder);
        userRepository.save(user);

        String key = "code:" + dto.getUsername();
        stringRedisTemplate.delete(key);
    }
}
