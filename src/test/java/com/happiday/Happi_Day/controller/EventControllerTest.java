package com.happiday.Happi_Day.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.happiday.Happi_Day.domain.controller.EventController;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventUpdateDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.service.EventService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;
    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    private ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());
    private User user1;

    private Artist artist1, artist2;

    private Team team1, team2;

    private Hashtag hashtag1, hashtag2;

    private MockMultipartFile thumbnailFile, imageFile, updateThumbnailFile, updateImageFile;


    private EventResponseDto mockEventResponseDto;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
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

        team2 = Team.builder()
                .name("소녀시대")
                .description("소녀시대입니다.")
                .build();

        hashtag1 = Hashtag.builder()
                .tag("동방신기포에버")
                .build();

        hashtag2 = Hashtag.builder()
                .tag("소녀시대포에버")
                .build();

        thumbnailFile = new MockMultipartFile(
                "thumbnailFile", "eventImage1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        imageFile = new MockMultipartFile(
                "imageFile", "eventImage2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        updateThumbnailFile = new MockMultipartFile(
                "thumbnailFile", "newEventImage1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        updateImageFile = new MockMultipartFile(
                "imageFile", "newEventImage2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

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

    }

    private String eventJsonRequest() {
        return "{"
                + "\"title\":\"good event\","
                + "\"startTime\":\"2023-08-31T10:00:00\","
                + "\"endTime\":\"2023-09-23T18:00:00\","
                + "\"description\":\"좋은 이벤트 설명\","
                + "\"address\":\"서울특별시 서초구 반포대로30길 32\","
                + "\"location\":\"1층 카페 이로\","
                + "\"hashtags\":[\"아이유\",\"김범수\",\"동방신기\",\"동방신기포에버\",\"소녀시대포에버\"]"
                + "}";
    }
    @Test
    @DisplayName("이벤트 생성 API 테스트")
    void createEventTest() throws Exception {
        // given
        MockMultipartFile eventRequest = new MockMultipartFile("event", "", MediaType.APPLICATION_JSON_VALUE, eventJsonRequest().getBytes());

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());
        when(eventService.createEvent(any(EventCreateDto.class), any(MultipartFile.class), any(MultipartFile.class), anyString()))
                .thenReturn(mockEventResponseDto);

        // when, then
        mockMvc.perform(multipart(HttpMethod.POST,"/api/v1/events")
                        .file(eventRequest)
                        .file(imageFile)
                        .file(thumbnailFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("이벤트 단일 조회 API 테스트")
    void readEventTest() throws Exception {
        // given
        Long testEventId = 1L;
        when(eventService.readEvent(anyString(), eq(testEventId)))
                .thenReturn(mockEventResponseDto);

        // when, then
        mockMvc.perform(get("/api/v1/events/" + testEventId))
                .andExpect(status().isOk()); // 200 OK 상태 코드 검증
    }

    @Test
    @DisplayName("이벤트 삭제 API 테스트")
    void deleteEventTest() throws Exception {
        // given
        Long testEventId = 1L;
        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());

        doNothing().when(eventService).deleteEvent(eq(testEventId), anyString());

        // when, then
        mockMvc.perform(delete("/api/v1/events/" + testEventId))
                .andExpect(status().isOk());
    }

    private Page<EventListResponseDto> eventMockListResponseDtoPages() {

        List<EventListResponseDto> mockResponseDtoList = new ArrayList<>();

        EventListResponseDto eventListDto1 = EventListResponseDto.builder()
                .id(1L)
                .nickname(user1.getUsername())
                .title("이벤트 제목 1")
                .updatedAt(LocalDateTime.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .location("서울")
                .thumbnailUrl(thumbnailFile.getName())
                .artists(List.of(artist1.getName(), artist2.getName()))
                .teams(List.of(team1.getName()))
                .hashtags(List.of(hashtag1.getTag(), hashtag2.getTag()))
                .build();

        EventListResponseDto eventListDto2 = EventListResponseDto.builder()
                .id(2L)
                .nickname(user1.getUsername())
                .title("이벤트 제목 2")
                .updatedAt(LocalDateTime.now())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(3))
                .location("경기")
                .thumbnailUrl(thumbnailFile.getName())
                .artists(List.of(artist2.getName()))
                .teams(List.of(team2.getName()))
                .hashtags(List.of(hashtag1.getTag(), hashtag2.getTag()))
                .build();

        mockResponseDtoList.add(eventListDto1);
        mockResponseDtoList.add(eventListDto2);

        return new PageImpl<>(mockResponseDtoList);
    }
    @Test
    @DisplayName("이벤트 목록 조회 API 테스트")
    void readEventsTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        String filter = "testFilter";
        String keyword = "testKeyword";

        Page<EventListResponseDto> mockResponseDtoPage = eventMockListResponseDtoPages();

        when(eventService.readEvents(pageable, filter, keyword)).thenReturn(mockResponseDtoPage);

        // when, then
        mockMvc.perform(get("/api/v1/events")
                        .param("filter", filter)
                        .param("keyword", keyword))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("진행 중인 이벤트 목록 조회 API 테스트")
    void readOngoingEventsTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        String filter = "testFilter";
        String keyword = "testKeyword";

        Page<EventListResponseDto> mockResponseDtoPage = eventMockListResponseDtoPages();

        when(eventService.readOngoingEvents(pageable, filter, keyword)).thenReturn(mockResponseDtoPage);

        // when, then
        mockMvc.perform(get("/api/v1/events/ongoing")
                        .param("filter", filter)
                        .param("keyword", keyword))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("구독한 아티스트/팀의 이벤트 목록 조회 API 테스트")
    void readEventsBySubscribedArtistsTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        String filter = "testFilter";
        String keyword = "testKeyword";

        Page<EventListResponseDto> mockResponseDtoPage = eventMockListResponseDtoPages();

        when(eventService.readEventsBySubscribedArtists(pageable, filter, keyword, user1.getUsername())).thenReturn(mockResponseDtoPage);

        // when, then
        mockMvc.perform(get("/api/v1/events/subscribedArtists")
                        .param("filter", filter)
                        .param("keyword", keyword))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("구독한 아티스트/팀의 진행 중인 이벤트 목록 조회 API 테스트")
    void readOngoingEventsBySubscribedArtistsTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        String filter = "testFilter";
        String keyword = "testKeyword";

        Page<EventListResponseDto> mockResponseDtoPage = eventMockListResponseDtoPages();

        when(eventService.readOngoingEventsBySubscribedArtists(pageable, filter, keyword, user1.getUsername())).thenReturn(mockResponseDtoPage);

        // when, then
        mockMvc.perform(get("/api/v1/events/subscribedArtists/ongoing")
                        .param("filter", filter)
                        .param("keyword", keyword))
                .andExpect(status().isOk());
    }

    private EventUpdateDto eventMockUpdateDto() {
        return EventUpdateDto.builder()
                .title("이벤트 바뀐 제목")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .description("수정된 내용")
                .address("수정된 주소")
                .location("서울")
                .thumbnailUrl(thumbnailFile.getName())
                .hashtags(List.of(hashtag1.getTag(), hashtag2.getTag(), "테스트해시태그", "아이유"))
                .build();

    }

    @Test
    @DisplayName("이벤트 수정 API 테스트")
    void updateEventTest() throws Exception {
        // given
        Long testEventId = 1L;
        EventUpdateDto eventUpdateDto = eventMockUpdateDto();

        MockMultipartFile eventDtoAsJson = new MockMultipartFile("event", "", "application/json", objectMapper.writeValueAsBytes(eventUpdateDto));

        when(eventService.updateEvent(eq(testEventId), any(EventUpdateDto.class), any(), any(), eq(user1.getUsername())))
                .thenReturn(mockEventResponseDto);

        // when, then
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/events/{eventId}", testEventId)
                        .file(eventDtoAsJson)
                        .file(updateThumbnailFile)
                        .file(updateThumbnailFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mockEventResponseDto)));
    }

    @Test
    @DisplayName("이벤트 좋아요 API 테스트")
    void likeEventTest() throws Exception {
        // given
        Long testEventId = 1L;
        String expectedResponse = "like / likecount : " + 1;

        when(eventService.likeEvent(testEventId, user1.getUsername())).thenReturn(expectedResponse);

        // when, then
        mockMvc.perform(post("/api/v1/events/{eventId}/like", testEventId))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString(expectedResponse)));
    }

    @Test
    @DisplayName("이벤트 참가하기 API 테스트")
    void joinEventTest() throws Exception {
        // given
        Long testEventId = 1L;
        String expectedResponse = mockEventResponseDto.getTitle() + "participation";

        when(SecurityUtils.getCurrentUsername()).thenReturn(user1.getUsername());

        when(eventService.joinEvent(testEventId, user1.getUsername())).thenReturn(expectedResponse);

        // when, then
        mockMvc.perform(post("/api/v1/events/" + testEventId + "/join"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString(expectedResponse)));
    }



}