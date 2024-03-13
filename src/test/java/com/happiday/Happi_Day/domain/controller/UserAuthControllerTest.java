package com.happiday.Happi_Day.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.UserFindDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserLoginDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserRegisterDto;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserAuthControllerTest {

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    private User testUser;

    @BeforeAll
    public static void beforeAll() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        securityUtilsMockedStatic.close();
    }

    @BeforeEach
    public void init() {
        this.testUser = User.builder()
                .username("test@email.com")
                .password(passwordEncoder.encode("qwer1234"))
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .role(RoleType.USER)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();

        userRepository.save(testUser);
    }

    @Test
    void 회원가입_성공() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 회원가입_실패_전화번호입력오류() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("010123456789");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Assertions.assertThat(userRepository.existsByUsername("user@email.com")).isFalse();
    }

    @Test
    void 회원가입_실패_이메일입력오류() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("useremail.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Assertions.assertThat(userRepository.existsByPhone("01012345678")).isFalse();
    }

    @Test
    void 회원가입_실패_이메일중복() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("test@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012345678");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());

        Assertions.assertThat(userRepository.existsByPhone("01012345678")).isFalse();
    }

    @Test
    void 회원가입_실패_전화번호중복() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("user@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("닉네임");
        dto.setRealname("가나다");
        dto.setPhone("01012341234");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/signup")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());

        Assertions.assertThat(userRepository.existsByUsername("user@email.com")).isFalse();
    }

    @Test
    void 로그인_성공() throws Exception {
        UserLoginDto dto = new UserLoginDto("test@email.com", "qwer1234");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<User> user = userRepository.findByUsername("test@email.com");

        Assertions.assertThat(user.get().getLastLoginAt()).isNotNull();
    }

    @Test
    void 로그인_실패_이메일불일치() throws Exception {
        UserLoginDto dto = new UserLoginDto("test1@email.com", "qwer1234");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 로그인_실패_비밀번호불일치() throws Exception {
        UserLoginDto dto = new UserLoginDto("test@email.com", "qwer12345");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/login")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void 로그아웃_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    void 회원가입관리자_성공() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("admin@email.com");
        dto.setPassword("qwer1234");
        dto.setNickname("관리자");
        dto.setRealname("김소영");
        dto.setPhone("01012344321");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/admin")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 비밀번호찾기_성공() throws Exception {
        UserFindDto dto = new UserFindDto("김철수", "test@email.com");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/auth/find")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 이메일확인_성공() throws Exception {
//        UserFindDto dto1 = new UserFindDto("김철수", "test@email.com");
//        String body1 = objectMapper.writeValueAsString(dto1);
//        MvcResult result = mockMvc.perform(post("/api/v1/auth/find")
//                        .content(body1)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String response = result.getResponse().getContentAsString();
//        UserNumDto dto2 = new UserNumDto("test@email.com", response);
//        String body2 = objectMapper.writeValueAsString(dto2);
//        mockMvc.perform(post("/api/v1/auth/check")
//                        .content(body2)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
    }

    @Test
    void 비밀번호변경_성공() throws Exception {
        UserLoginDto dto = new UserLoginDto("test@email.com", "newpassword");
        String body = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/api/v1/auth/password")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }
}