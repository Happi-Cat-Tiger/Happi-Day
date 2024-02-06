package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.CustomUserDetails;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final JpaUserDetailsManager manager;

    @Transactional
    public void createUser(UserRegisterDto dto) {
        // 유효성 검사
        checkValidEmail(dto.getUsername());
        checkValidPhone(dto.getPhone());

        // DB 확인
        checkDuplicatedUsername(dto.getUsername());
        checkDuplicatedNickname(dto.getNickname());
        checkDuplicatedPhone(dto.getPhone());

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .realname(dto.getRealname())
                .phone(dto.getPhone())
                .role(RoleType.USER)
                .imageUrl(defaultImageUtils.getDefaultImageUrlUserProfile())
                .build();
        manager.createUser(userDetails);
    }

    @Transactional
    public void createAdmin(UserRegisterDto dto) {
        // 유효성 검사
        checkValidEmail(dto.getUsername());
        checkValidPhone(dto.getPhone());

        // DB 확인
        checkDuplicatedUsername(dto.getUsername());
        checkDuplicatedNickname(dto.getNickname());
        checkDuplicatedPhone(dto.getPhone());

        CustomUserDetails userDetails = CustomUserDetails.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .realname(dto.getRealname())
                .phone(dto.getPhone())
                .role(RoleType.ADMIN)
                .imageUrl(defaultImageUtils.getDefaultImageUrlUserProfile())
                .build();
        manager.createUser(userDetails);
    }

    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto updateUserProfile(String username, UserUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 유효성 검사
        checkValidPhone(dto.getPhone());

        // DB 확인
        checkDuplicatedNickname(dto.getNickname());
        checkDuplicatedPhone(dto.getPhone());

        user.update(dto.toEntity(), passwordEncoder);

        userRepository.save(user);
        return UserResponseDto.fromEntity(user);
    }

    public void checkValidPhone(String phone) {
        String phoneRegex = "\\d{11}";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);

        if (!matcher.matches()) {
            throw new CustomException(ErrorCode.PHONE_FORMAT_ERROR);
        }
    }

    public void checkValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);

        if (!matcher.matches()) {
            throw new CustomException(ErrorCode.EMAIL_FORMAT_ERROR);
        }
    }

    public void checkDuplicatedNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.NICKNAME_CONFLICT);
        }
    }

    public void checkDuplicatedPhone(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new CustomException(ErrorCode.PHONE_CONFLICT);
        }
    }

    public void checkDuplicatedUsername(String username) {
        if (userRepository.existsByUsername(username))
            throw new CustomException(ErrorCode.USER_CONFLICT);
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
