package com.happiday.Happi_Day.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.controller.EventCommentController;
import com.happiday.Happi_Day.domain.controller.EventReviewController;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewUpdateDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.service.EventCommentService;
import com.happiday.Happi_Day.domain.service.EventReviewService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class EventReviewControllerTest {

    @InjectMocks
    private EventReviewController eventReviewController;

    @Mock
    private EventReviewService eventReviewService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private User user1;

    private Artist artist1, artist2;

    private Team team1;

    private Hashtag hashtag1, hashtag2;

    private MockMultipartFile imageFile;


    private EventResponseDto mockEventResponseDto;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private EventCommentResponseDto mockCommentResponseDto;

    private EventReviewResponseDto mockReviewResponseDto, mockUpdatedReviewResponseDto;

    @BeforeEach
    public void init() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventReviewController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    @BeforeAll
    public static void beforeAll() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        securityUtilsMockedStatic.close();
    }
    @BeforeEach
    void setup() {
        user1 = User.builder()
                .username("user1@email.com")
                .password("qwe123")
                .nickname("왕감자")
                .realname("이용순")
                .phone("01012343214")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();

        artist1 = Artist.builder()
                .name("아이유")
                .description("아이유입니다.")
                .build();

        artist2 = Artist.builder()
                .name("김범수")
                .description("김범수입니다.")
                .build();


        team1 = Team.builder()
                .name("동방신기")
                .description("동방신기입니다.")
                .build();

        hashtag1 = Hashtag.builder()
                .tag("동방신기포에버")
                .build();

        hashtag2 = Hashtag.builder()
                .tag("소녀시대포에버")
                .build();

        imageFile = new MockMultipartFile(
                "imageFile", "eventImage2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        LocalDateTime startTime = LocalDateTime.parse("2023-08-31T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse("2023-09-23T18:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockEventResponseDto = EventResponseDto.builder()
                .id(1L)
                .username(user1.getUsername())
                .title("good event")
                .startTime(startTime)
                .endTime(endTime)
                .description("좋은 이벤트 설명")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl(String.valueOf(imageFile))
                .artists(List.of(artist1.getName(), artist2.getName()))
                .teams(List.of(team1.getName()))
                .hashtags(List.of(hashtag1.getTag(), hashtag2.getTag()))
                .build();

        mockCommentResponseDto = EventCommentResponseDto.builder()
                .id(1L)
                .username(user1.getNickname())
                .content("good event")
                .updatedAt(LocalDateTime.now())
                .build();


        mockReviewResponseDto = EventReviewResponseDto.builder()
                .id(1L)
                .username(user1.getNickname())
                .description("nice event")
                .rating(5)
                .updatedAt(LocalDateTime.now())
                .build();

        mockUpdatedReviewResponseDto = EventReviewResponseDto.builder()
                .id(1L)
                .username(user1.getNickname())
                .description("bad event")
                .rating(3)
                .updatedAt(LocalDateTime.now())
                .build();

        mockReviewResponseDto.setImageUrlList(List.of("http://example.com/eventImage.jpg"));

    }

    @Test
    @DisplayName("리뷰 작성 API 테스트")
    void createReviewTest() throws Exception {
        Long eventId = 1L;

        EventReviewCreateDto createDto = new EventReviewCreateDto("good event", 5);

        MockMultipartFile review = new MockMultipartFile("review", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(createDto));
        MockMultipartFile file = new MockMultipartFile("imageFiles", "eventImage2.jpg",
                MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());

        when(eventReviewService.createReview(eq(eventId), any(EventReviewCreateDto.class), anyList(), anyString()))
                .thenReturn(mockReviewResponseDto);

        mockMvc.perform(multipart(HttpMethod.POST,"/api/v1/events/{eventId}/reviews", eventId)
                        .file(review)
                        .file(file))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 조회 API 테스트")
    void readReviewTest() throws Exception {
        Long eventId = 1L;
        Long reviewId = 1L;

        when(eventReviewService.readReview(anyLong(), anyLong()))
                .thenReturn(mockReviewResponseDto);

        mockMvc.perform(get("/api/v1/events/{eventId}/reviews/{reviewId}", eventId, reviewId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("nice event"))
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 수정 API 테스트")
    void updateReviewTest() throws Exception {
        Long eventId = 1L;
        Long reviewId = 1L;

        EventReviewUpdateDto updateDto = new EventReviewUpdateDto("Updated description", 4);
        MockMultipartFile jsonFile = new MockMultipartFile("review", "",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(updateDto));
        MockMultipartFile file = new MockMultipartFile("imageFiles", "updatedEventImage.jpg",
                MediaType.IMAGE_JPEG_VALUE, "UpdatedImageData".getBytes());

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());

        when(eventReviewService.updateReview(eq(eventId), eq(reviewId), any(EventReviewUpdateDto.class), anyString(), anyList()))
                .thenReturn(mockUpdatedReviewResponseDto);

        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/events/{eventId}/reviews/{reviewId}", eventId, reviewId)
                        .file(jsonFile)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("bad event"))
                .andDo(print());
    }

    @Test
    @DisplayName("리뷰 삭제 API 테스트")
    void deleteReviewTest() throws Exception {
        Long eventId = 1L;
        Long reviewId = 1L;
        doNothing().when(eventReviewService).deleteReview(anyLong(), anyLong(), anyString());

        mockMvc.perform(delete("/api/v1/events/{eventId}/reviews/{reviewId}", eventId, reviewId))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted done"))
                .andDo(print());
    }
}
