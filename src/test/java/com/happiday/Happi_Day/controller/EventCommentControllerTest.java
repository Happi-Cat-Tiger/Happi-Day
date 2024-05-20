package com.happiday.Happi_Day.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.controller.EventCommentController;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentUpdateDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.service.EventCommentService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class EventCommentControllerTest {

    @InjectMocks
    private EventCommentController eventCommentController;

    @Mock
    private EventCommentService eventCommentService;

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

    @BeforeEach
    public void init() {
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders
                .standaloneSetup(eventCommentController)
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


    }

    @Test
    @DisplayName("댓글 작성 API 테스트")
    void createCommentTest() throws Exception {
        Long eventId = 1L;
        EventCommentCreateDto createDto = new EventCommentCreateDto("good event");


        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());
        when(eventCommentService.createComment(any(Long.class), any(EventCommentCreateDto.class), anyString()))
                .thenReturn(mockCommentResponseDto);

        mockMvc.perform(post("/api/v1/events/{eventId}/comments", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("good event"));
    }

    private Page<EventCommentResponseDto> commentMockListResponseDtoPages() {
        Long eventId = 1L;

        List<EventCommentResponseDto> mockResponseDtoList = new ArrayList<>();

        EventCommentResponseDto commentResponseDto1 = EventCommentResponseDto.builder()
                .id(1L)
                .content("comment List 1")
                .updatedAt(LocalDateTime.now())
                .build();

        EventCommentResponseDto commentResponseDto2 = EventCommentResponseDto.builder()
                .id(2L)
                .content("comment List 2")
                .updatedAt(LocalDateTime.now())
                .build();

        EventCommentResponseDto commentResponseDto3 = EventCommentResponseDto.builder()
                .id(3L)
                .content("comment List 3")
                .updatedAt(LocalDateTime.now())
                .build();

        mockResponseDtoList.add(commentResponseDto1);
        mockResponseDtoList.add(commentResponseDto2);
        mockResponseDtoList.add(commentResponseDto3);

        return new PageImpl<>(mockResponseDtoList);
    }
    @Test
    @DisplayName("댓글 목록 조회 API 테스트")
    void readCommentsTest() throws Exception {
        Long eventId = 1L;
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        Page<EventCommentResponseDto> mockResponseDtoPage = commentMockListResponseDtoPages();

        when(eventCommentService.readComments(eventId, pageable)).thenReturn(mockResponseDtoPage);

        ResponseEntity<Page<EventCommentResponseDto>> response = eventCommentController.readComments(eventId, pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(eventCommentService).readComments(eventId, pageable);

        mockMvc.perform(get("/api/v1/events/{eventId}/comments", eventId))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private EventCommentUpdateDto eventCommentUpdateDto() {
        return EventCommentUpdateDto.builder()
                .content("good event")
                .build();
    }
    @Test
    @DisplayName("댓글 수정 API 테스트")
    void updateCommentTest() throws Exception {
        Long commentId = 1L;
        Long eventId = 1L;
        EventCommentUpdateDto updateDto = eventCommentUpdateDto();

        ObjectMapper objectMapper = new ObjectMapper();
        String updateDtoJson = objectMapper.writeValueAsString(updateDto);

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());
        when(eventCommentService.updateComment(eq(eventId), eq(commentId), any(EventCommentUpdateDto.class), eq(user1.getUsername())))
                .thenReturn(mockCommentResponseDto);

        mockMvc.perform(put("/api/v1/events/{eventId}/comments/{commentId}", eventId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateDtoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("good event"))
                .andDo(print());

        verify(eventCommentService).updateComment(eq(eventId), eq(commentId), any(EventCommentUpdateDto.class), eq(user1.getUsername()));
    }

    @Test
    @DisplayName("댓글 삭제 API 테스트")
    void deleteCommentTest() throws Exception {
        Long commentId = 1L;
        Long eventId = 1L;

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());
        doNothing().when(eventCommentService).deleteComment(eq(eventId), eq(commentId), anyString());

        mockMvc.perform(delete("/api/v1/events/{eventId}/comments/{commentId}", eventId, commentId))
                .andExpect(status().isOk())
                .andExpect(content().string("deleted done"))
                .andDo(print());

        verify(eventCommentService).deleteComment(eq(eventId), eq(commentId), eq(user1.getUsername()));
    }
}
