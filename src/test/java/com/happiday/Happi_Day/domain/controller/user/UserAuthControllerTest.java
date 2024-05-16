//package com.happiday.Happi_Day.domain.controller.user;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.happiday.Happi_Day.domain.entity.user.RoleType;
//import com.happiday.Happi_Day.domain.entity.user.User;
//import com.happiday.Happi_Day.domain.entity.user.dto.UserFindDto;
//import com.happiday.Happi_Day.domain.entity.user.dto.UserLoginDto;
//import com.happiday.Happi_Day.domain.entity.user.dto.UserNumDto;
//import com.happiday.Happi_Day.domain.entity.user.dto.UserRegisterDto;
//import com.happiday.Happi_Day.domain.repository.UserRepository;
//import com.happiday.Happi_Day.utils.SecurityUtils;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//@ActiveProfiles("test")
//class UserAuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
//
//    private User testUser;
//
//    @Value("${mail.address}")
//    private String testEmail;
//
//    @BeforeAll
//    public static void beforeAll() {
//        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        securityUtilsMockedStatic.close();
//    }
//
//    @BeforeEach
//    public void init() {
//        this.testUser = User.builder()
//                .username(testEmail)
//                .password(passwordEncoder.encode("qwer1234"))
//                .nickname("테스트")
//                .realname("김철수")
//                .phone("01012341234")
//                .role(RoleType.USER)
//                .isTermsAgreed(true)
//                .termsAt(LocalDateTime.now())
//                .build();
//
//        userRepository.save(testUser);
//    }
//
//    @Test
//    void 회원가입_성공() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("user@email.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("닉네임");
//        dto.setRealname("가나다");
//        dto.setPhone("01012345678");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        Optional<User> user = userRepository.findByUsername("user@email.com");
//        Assertions.assertThat(user.get().getRole()).isEqualTo(RoleType.USER);
//    }
//
//    @Test
//    void 회원가입_실패_전화번호입력오류() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("user@email.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("닉네임");
//        dto.setRealname("가나다");
//        dto.setPhone("010123456789");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//
//        Assertions.assertThat(userRepository.existsByUsername("user@email.com")).isFalse();
//    }
//
//    @Test
//    void 회원가입_실패_이메일입력오류() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("useremail.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("닉네임");
//        dto.setRealname("가나다");
//        dto.setPhone("01012345678");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//
//        Assertions.assertThat(userRepository.existsByPhone("01012345678")).isFalse();
//    }
//
//    @Test
//    void 회원가입_실패_이메일중복() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername(testEmail);
//        dto.setPassword("qwer1234");
//        dto.setNickname("닉네임");
//        dto.setRealname("가나다");
//        dto.setPhone("01012345678");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isConflict());
//
//        Assertions.assertThat(userRepository.existsByPhone("01012345678")).isFalse();
//    }
//
//    @Test
//    void 회원가입_실패_전화번호중복() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("user@email.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("닉네임");
//        dto.setRealname("가나다");
//        dto.setPhone("01012341234");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/signup")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isConflict());
//
//        Assertions.assertThat(userRepository.existsByUsername("user@email.com")).isFalse();
//    }
//
//    @Test
//    void 로그인_성공() throws Exception {
//        UserLoginDto dto = new UserLoginDto(testEmail, "qwer1234");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        Optional<User> user = userRepository.findByUsername(testEmail);
//
//        Assertions.assertThat(user.get().getLastLoginAt()).isNotNull();
//    }
//
//    @Test
//    void 로그인_실패_이메일불일치() throws Exception {
//        UserLoginDto dto = new UserLoginDto("test1@email.com", "qwer1234");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void 로그인_실패_비밀번호불일치() throws Exception {
//        UserLoginDto dto = new UserLoginDto(testEmail, "qwer12345");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void 로그아웃_성공() throws Exception {
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        mockMvc.perform(get("/api/v1/auth/logout"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void 회원가입관리자_성공() throws Exception {
//        UserRegisterDto dto = new UserRegisterDto();
//        dto.setUsername("admin@email.com");
//        dto.setPassword("qwer1234");
//        dto.setNickname("관리자");
//        dto.setRealname("김소영");
//        dto.setPhone("01012344321");
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/admin")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isCreated());
//
//        Optional<User> user = userRepository.findByUsername("admin@email.com");
//        Assertions.assertThat(user.get().getRole()).isEqualTo(RoleType.ADMIN);
//    }
//
//    @Test
//    void 본인인증후인증번호발송_성공() throws Exception {
//        UserFindDto dto = new UserFindDto("김철수", testEmail);
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        mockMvc.perform(post("/api/v1/auth/find")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void 본인인증후인증번호발송_실패_입력값오류() throws Exception {
//        UserFindDto dto = new UserFindDto("김철", testEmail);
//
//        String body = objectMapper.writeValueAsString(dto);
//
//        MvcResult result = mockMvc.perform(post("/api/v1/auth/find")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isNotFound())
//                .andReturn();
//    }
//
//    @Test
//    void 인증번호값일치확인_성공() throws Exception {
//        UserFindDto userFindDto = new UserFindDto("김철수", testEmail);
//        String userFindBody = objectMapper.writeValueAsString(userFindDto);
//        MvcResult result = mockMvc.perform(post("/api/v1/auth/find")
//                        .content(userFindBody)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String code = result.getResponse().getContentAsString();
//        UserNumDto dto = new UserNumDto(testEmail, code);
//        String body = objectMapper.writeValueAsString(dto);
//        mockMvc.perform(post("/api/v1/auth/check")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void 인증번호값일치확인_실패_입력오류() throws Exception {
//        UserFindDto userFindDto = new UserFindDto("김철수", testEmail);
//        String userFindBody = objectMapper.writeValueAsString(userFindDto);
//        MvcResult result = mockMvc.perform(post("/api/v1/auth/find")
//                        .content(userFindBody)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String code = result.getResponse().getContentAsString();
//        UserNumDto dto = new UserNumDto(testEmail, code+"1");
//        String body = objectMapper.writeValueAsString(dto);
//        mockMvc.perform(post("/api/v1/auth/check")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void 비밀번호변경_성공() throws Exception {
//        UserLoginDto dto = new UserLoginDto(testEmail, "newpassword");
//        String body = objectMapper.writeValueAsString(dto);
//        mockMvc.perform(post("/api/v1/auth/password")
//                        .content(body)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//        Optional<User> user = userRepository.findByUsername(testEmail);
//        Assertions.assertThat(passwordEncoder.matches("newpassword", user.get().getPassword())).isTrue();
//    }
//
//    @Test
//    void 이용약관() throws Exception {
//        mockMvc.perform(get("/api/v1/auth/terms"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//}