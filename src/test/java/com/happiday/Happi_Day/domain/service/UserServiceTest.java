package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.*;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DefaultImageUtils defaultImageUtils;

    @Autowired
    PasswordEncoder passwordEncoder;

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
    @DisplayName("회원가입")
    @Transactional
    void createUser() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        // when
        userService.createUser(dto);

        // then
        Optional<User> user = userRepository.findByUsername(dto.getUsername());
        Assertions.assertThat(user.get().getUsername()).isEqualTo("user@email.com");
    }

    @Test
    @DisplayName("회원가입 전화번호 입력 오류")
    @Transactional
    void createUser_isValidPhone() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("010123456789");

        // when // then
         Assertions.assertThatThrownBy(() -> userService.createUser(dto))
                 .hasMessage(ErrorCode.PHONE_FORMAT_ERROR.getMessage());

    }

    @Test
    @DisplayName("회원가입 이메일 입력 오류")
    @Transactional
    void createUser_isValidUsername() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@emailcom");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("010123456789");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.createUser(dto))
                .hasMessage(ErrorCode.EMAIL_FORMAT_ERROR.getMessage());

    }

    @Test
    @DisplayName("회원가입 이메일 중복 오류")
    @Transactional
    void createUser_isDuplicatedUsername() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("test@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.createUser(dto))
                .hasMessage(ErrorCode.USER_CONFLICT.getMessage());

    }

    @Test
    @DisplayName("회원가입 닉네임 중복 오류")
    @Transactional
    void createUser_isDuplicatedNickname() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("테스트");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.createUser(dto))
                .hasMessage(ErrorCode.NICKNAME_CONFLICT.getMessage());

    }

    @Test
    @DisplayName("회원가입 전화번호 중복 오류")
    @Transactional
    void createUser_isDuplicatedPhone() {
        // given
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012341234");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.createUser(dto))
                .hasMessage(ErrorCode.PHONE_CONFLICT.getMessage());

    }

    @Test
    @DisplayName("회원정보 수정")
    @Transactional
    void updateUser() {
        // given
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("닉네임2")
                .password("qwer12345")
                .phone("01012341111")
                .build();

        // when
        userService.updateUserProfile("test@email.com", dto);

        // then
        Optional<User> user = userRepository.findByUsername("test@email.com");
        Assertions.assertThat(dto.getNickname()).isEqualTo(user.get().getNickname());
        Assertions.assertThat(dto.getPhone()).isEqualTo(user.get().getPhone());
    }

    @Test
    @DisplayName("회원정보 이미지 수정")
    @Transactional
    void changeProfileImage() {
        // given
        MultipartFile file = new MockMultipartFile(
                "profile", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes()
        );

        // when
        UserResponseDto result = userService.changeImage("test@email.com", file);

        // then
        Assertions.assertThat(result.getImageUrl()).isNotNull();
        Assertions.assertThat(result.getImageUrl()).isNotEqualTo(defaultImageUtils.getDefaultImageUrlUserProfile());
    }

    @Test
    @DisplayName("회원정보 기본 이미지로 변경")
    @Transactional
    void changeProfileImageDefault() {
        // given

        // when
        UserResponseDto result = userService.resetImage("test@email.com");

        // then
        Assertions.assertThat(result.getImageUrl()).isNotNull();
        Assertions.assertThat(result.getImageUrl()).isEqualTo(defaultImageUtils.getDefaultImageUrlUserProfile());
    }

    @Test
    @DisplayName("회원탈퇴")
    @Transactional
    void deleteUser() {
        // given
        UserPWDto dto = new UserPWDto("qwer1234");

        // when
        userService.deleteUser("test@email.com", dto);

        // then
        Assertions.assertThat(userRepository.existsByUsername("test@email.com")).isFalse();
    }

    @Test
    @DisplayName("비밀번호 찾기를 위한 본인 인증 후 이메일로 인증번호 전송")
    @Transactional
    void findPassword() {
        // given
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");

        // when
        String result = userService.findPassword(dto);

        // then
        Assertions.assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("이메일로 온 인증번호와 입력한 인증번호 비교")
    @Transactional
    void checkEmail() {
        // given
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");
        String number = userService.findPassword(dto);

        UserNumDto dto2 = new UserNumDto("test@email.com", number);
        UserNumDto dto3 = new UserNumDto("test@email.com", "1111111");

        // when
        Boolean result1 = userService.checkEmail(dto2);

        // then
        Assertions.assertThat(result1).isTrue();
        Assertions.assertThatThrownBy(() -> userService.checkEmail(dto3))
                .hasMessage(ErrorCode.CODE_NOT_MATCHED.getMessage());
    }

    @Test
    @DisplayName("인증 완료 후 비밀번호 변경")
    @Transactional
    void changePassword() {
        // given
        UserLoginDto dto = new UserLoginDto("test@email.com", "test1234");

        // when
        userService.changePassword(dto);

        // then
        Optional<User> user = userRepository.findByUsername("test@email.com");
        Assertions.assertThat(passwordEncoder.matches("test1234", user.get().getPassword())).isTrue();
    }
}