package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.*;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DefaultImageUtils defaultImageUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    void 회원가입_성공(){
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
    void 회원가입_실패_전화번호입력오류() {
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
    void 회원가입_실패_이메일입력오류() {
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
    void 회원가입_실패_이메일중복() {
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
    void 회원가입_실패_닉네임중복() {
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
    void 회원가입_실패_전화번호중복() {
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
    void 회원정보수정_성공() {
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
    void 회원정보수정_실패_닉네임중복() {
        // given
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("테스트")
                .password("qwer12345")
                .phone("01012341111")
                .build();

        // when // then
        Assertions.assertThatThrownBy(() -> userService.updateUserProfile("test@email.com", dto))
                .hasMessage(ErrorCode.NICKNAME_CONFLICT.getMessage());
    }

    @Test
    void 회원정보수정_실패_전화번호중복() {
        // given
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("닉네임2")
                .password("qwer12345")
                .phone("01012341234")
                .build();

        // when // then
        Assertions.assertThatThrownBy(() -> userService.updateUserProfile("test@email.com", dto))
                .hasMessage(ErrorCode.PHONE_CONFLICT.getMessage());
    }

    @Test
    void 회원정보수정_실패_전화번호입력오류() {
        // given
        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("닉네임2")
                .password("qwer12345")
                .phone("010123411110")
                .build();

        // when // then
        Assertions.assertThatThrownBy(() -> userService.updateUserProfile("test@email.com", dto))
                .hasMessage(ErrorCode.PHONE_FORMAT_ERROR.getMessage());
    }

    @Test
    void 회원정보_프로필이미지수정_성공() {
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
    void 회원정보_프로필이미지초기화_성공() {
        // given

        // when
        UserResponseDto result = userService.resetImage("test@email.com");

        // then
        Assertions.assertThat(result.getImageUrl()).isNotNull();
        Assertions.assertThat(result.getImageUrl()).isEqualTo(defaultImageUtils.getDefaultImageUrlUserProfile());
    }

    @Test
    void 회원탈퇴_성공() {
        // given
        UserPWDto dto = new UserPWDto("qwer1234");

        // when
        userService.deleteUser("test@email.com", dto);

        // then
        Assertions.assertThat(userRepository.existsByUsername("test@email.com")).isFalse();
    }

    @Test
    void 회원탈퇴_실패() {
        // given
        UserPWDto dto = new UserPWDto("qwer12345");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.deleteUser("test@email.com", dto))
                .hasMessage(ErrorCode.PASSWORD_NOT_MATCHED.getMessage());
    }

    @Test
    void 비밀번호찾기_성공_본인인증_인증번호발송() {
        // given
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");

        // when
        String result = userService.findPassword(dto);

        // then
        Assertions.assertThat(result).isNotNull();

        String key = "code:" + dto.getUsername();
        Assertions.assertThat(redisTemplate.opsForValue().get(key)).isNotNull();
    }

    @Test
    void 비밀번호찾기_실패_본인인증오류() {
        // given
        UserFindDto dto1 = new UserFindDto("김철", "test@email.com");
        UserFindDto dto2 = new UserFindDto("김철수", "testt@email.com");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.findPassword(dto1))
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
        Assertions.assertThatThrownBy(() -> userService.findPassword(dto2))
                .hasMessage(ErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 비밀번호찾기_성공_인증번호입력() {
        // given
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");
        String number = userService.findPassword(dto);

        UserNumDto dto2 = new UserNumDto("test@email.com", number);

        // when
        Boolean result = userService.checkEmail(dto2);

        // then
        Assertions.assertThat(result).isTrue();

        String key = "code:" + dto.getUsername();
        Assertions.assertThat(redisTemplate.opsForValue().get(key)).isNotNull();
    }

    @Test
    void 비밀번호찾기_실패_인증번호입력_오류() {
        // given
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");
        String number = userService.findPassword(dto);

        UserNumDto dto2 = new UserNumDto("test@email.com", "1111111");

        // when // then
        Assertions.assertThatThrownBy(() -> userService.checkEmail(dto2))
                .hasMessage(ErrorCode.CODE_NOT_MATCHED.getMessage());
    }

    @Test
    void 비밀번호찾기_성공_비밀번호변경() {
        // given
        UserLoginDto dto = new UserLoginDto("test@email.com", "test1234");

        // when
        userService.changePassword(dto);

        // then
        Optional<User> user = userRepository.findByUsername("test@email.com");
        Assertions.assertThat(passwordEncoder.matches("test1234", user.get().getPassword())).isTrue();

        String key = "code:" + dto.getUsername();
        Assertions.assertThat(redisTemplate.opsForValue().get(key)).isNull();

    }
}