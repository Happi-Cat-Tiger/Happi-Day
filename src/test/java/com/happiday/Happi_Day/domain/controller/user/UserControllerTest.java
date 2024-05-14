package com.happiday.Happi_Day.domain.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.UserPWDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserUpdateDto;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DefaultImageUtils defaultImageUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private User testUser;

    @Value("${mail.address}")
    private String testEmail;

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
                .username(testEmail)
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
    void 회원정보조회_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/info"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 회원정보수정_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("테스트2")
                .phone("01012345678")
                .password("qwer12345")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/v1/user/info")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(dto.getNickname()).isEqualTo(user.get().getNickname());
        Assertions.assertThat(dto.getPhone()).isEqualTo(user.get().getPhone());
    }

    @Test
    void 회원정보수정_실패_닉네임중복() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("테스트")
                .phone("01012345678")
                .password("qwer12345")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/v1/user/info")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(dto.getPhone()).isNotEqualTo(user.get().getPhone());
    }

    @Test
    void 회원정보수정_실패_전화번호중복() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("테스트2")
                .phone("01012341234")
                .password("qwer12345")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/v1/user/info")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(dto.getNickname()).isNotEqualTo(user.get().getNickname());
    }

    @Test
    void 회원정보수정_실패_전화번호입력오류() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserUpdateDto dto = UserUpdateDto.builder()
                .nickname("테스트2")
                .phone("010123456789")
                .password("qwer12345")
                .build();

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(patch("/api/v1/user/info")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(dto.getNickname()).isNotEqualTo(user.get().getNickname());
        Assertions.assertThat(dto.getPhone()).isNotEqualTo(user.get().getPhone());
    }

    @Test
    void 회원정보이미지수정_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        MockMultipartFile profileImage = new MockMultipartFile(
                "multipartFile", "image.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes()
        );

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/user/info/image")
                .file(profileImage)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(user.get().getImageUrl()).isNotNull();
        Assertions.assertThat(user.get().getImageUrl()).isNotEqualTo(defaultImageUtils.getDefaultImageUrlUserProfile());
    }

    @Test
    void 회원정보이미지초기화_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/v1/user/info/default"))
                .andDo(print())
                .andExpect(status().isOk());

        Optional<User> user = userRepository.findByUsername(testEmail);

        Assertions.assertThat(user.get().getImageUrl()).isNotNull();
        Assertions.assertThat(user.get().getImageUrl()).isEqualTo(defaultImageUtils.getDefaultImageUrlUserProfile());
    }

    @Test
    void 회원탈퇴_성공() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserPWDto dto = new UserPWDto("qwer1234");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(delete("/api/v1/user/withdrawal")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(userRepository.existsByUsername(testEmail)).isFalse();
    }

    @Test
    void 회원탈퇴_실패() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UserPWDto dto = new UserPWDto("qwer12345");

        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(delete("/api/v1/user/withdrawal")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        Assertions.assertThat(userRepository.existsByUsername(testEmail)).isTrue();
    }

    @Test
    @DisplayName("마이페이지 - 내 게시물")
    void getMyArticles() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/articles"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 게시물 댓글")
    void getMyArticleComments() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/articles/comments"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 좋아요한 게시물")
    void getLikeArticles() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/articles/like"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 이벤트")
    void getMyEvents() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/events"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 이벤트 댓글")
    void getMyEventComments() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/events/comments"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 좋아요한 이벤트")
    void getLikeEvents() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/events/like"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 참여한 이벤트")
    void getJoinEvents() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/events/join"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 이벤트 리뷰")
    void getMyReviews() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/reviews"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 판매글")
    void getMySales() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/sales"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 좋아요한 판매글")
    void getLikeSales() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/sales/like"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("마이페이지 - 내 주문내역")
    void getMyOrders() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/user/orders"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}