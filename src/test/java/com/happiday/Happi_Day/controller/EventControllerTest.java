package com.happiday.Happi_Day.controller;

import com.happiday.Happi_Day.domain.controller.EventController;
import com.happiday.Happi_Day.domain.controller.UserController;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.service.EventService;
import com.happiday.Happi_Day.jwt.JwtTokenFilter;
import com.happiday.Happi_Day.jwt.JwtTokenUtils;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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


    private User user1;

    private Artist artist1, artist2;

    private Team team1, team2;

    private Hashtag hashtag1, hashtag2;

    private MockMultipartFile thumbnailFile, imageFile;

    private Event event1;

    private EventResponseDto mockEventResponseDto;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
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

//        event1 = Event.builder()
//                .title("제목")
//                .user(user1)
//                .startTime(LocalDateTime.now().minusMonths(1))
//                .endTime(LocalDateTime.now().plusMonths(3))
//                .description("내용")
//                .address("서울특별시 서초구 반포대로30길 32")
//                .location("1층 카페 이로")
//                .imageUrl(String.valueOf(eventImage1))
//                .eventHashtags(new ArrayList<>())
//                .build();


        LocalDateTime startTime = LocalDateTime.parse("2023-08-31T10:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse("2023-09-23T18:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        mockEventResponseDto = EventResponseDto.builder()
                .id(1L)
                .username(user1.getUsername())
                .title("좋은 이벤트")
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
                + "\"title\":\"좋은 이벤트\","
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
        // givne
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
                .thenReturn(mockEventResponseDto); // 서비스 메소드 호출 결과 모킹

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
//
//    @Test
//    @DisplayName("이벤트 목록 조회 API 테스트")
//    void readEventsTest() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 10); // 페이지 정보 설정
//        String filter = "someFilter"; // 필터 설정
//        String keyword = "someKeyword"; // 키워드 설정
//
//        Page<EventListResponseDto> mockResponseDtoPage = ...; // Mock 데이터 생성
//
//        // 이벤트 서비스의 readEvents 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.readEvents(pageable, filter, keyword)).thenReturn(mockResponseDtoPage);
//
//        // when, then
//        mockMvc.perform(get("/api/v1/events")
//                        .param("filter", filter)
//                        .param("keyword", keyword))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("진행 중인 이벤트 목록 조회 API 테스트")
//    void readOngoingEventsTest() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 10); // 페이지 정보 설정
//        String filter = "someFilter"; // 필터 설정
//        String keyword = "someKeyword"; // 키워드 설정
//
//        Page<EventListResponseDto> mockResponseDtoPage = ...; // Mock 데이터 생성
//
//        // 이벤트 서비스의 readOngoingEvents 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.readOngoingEvents(pageable, filter, keyword)).thenReturn(mockResponseDtoPage);
//
//        // when, then
//        mockMvc.perform(get("/api/v1/events/ongoing")
//                        .param("filter", filter)
//                        .param("keyword", keyword))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("구독한 아티스트/팀의 이벤트 목록 조회 API 테스트")
//    void readEventsBySubscribedArtistsTest() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 10); // 페이지 정보 설정
//        String filter = "someFilter"; // 필터 설정
//        String keyword = "someKeyword"; // 키워드 설정
//
//        Page<EventListResponseDto> mockResponseDtoPage = ...; // Mock 데이터 생성
//
//        // 이벤트 서비스의 readEventsBySubscribedArtists 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.readEventsBySubscribedArtists(pageable, filter, keyword, anyString())).thenReturn(mockResponseDtoPage);
//
//        // when, then
//        mockMvc.perform(get("/api/v1/events/subscribedArtists")
//                        .param("filter", filter)
//                        .param("keyword", keyword))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("구독한 아티스트/팀의 진행 중인 이벤트 목록 조회 API 테스트")
//    void readOngoingEventsBySubscribedArtistsTest() throws Exception {
//        // given
//        Pageable pageable = PageRequest.of(0, 10); // 페이지 정보 설정
//        String filter = "someFilter"; // 필터 설정
//        String keyword = "someKeyword"; // 키워드 설정
//
//        Page<EventListResponseDto> mockResponseDtoPage = ...; // Mock 데이터 생성
//
//        // 이벤트 서비스의 readOngoingEventsBySubscribedArtists 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.readOngoingEventsBySubscribedArtists(pageable, filter, keyword, anyString())).thenReturn(mockResponseDtoPage);
//
//        // when, then
//        mockMvc.perform(get("/api/v1/events/subscribedArtists/ongoing")
//                        .param("filter", filter)
//                        .param("keyword", keyword))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("이벤트 수정 API 테스트")
//    void updateEventTest() throws Exception {
//        // given
//        Long testEventId = 1L; // 테스트할 이벤트 ID
//        EventUpdateDto eventUpdateDto = ...; // 이벤트 업데이트 DTO 생성
//        MockMultipartFile thumbnailFile = ...; // 썸네일 파일 생성
//        MockMultipartFile imageFile = ...; // 이미지 파일 생성
//
//        EventResponseDto mockResponseDto = ...; // Mock 응답 DTO 생성
//
//        // 이벤트 서비스의 updateEvent 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.updateEvent(eq(testEventId), eq(eventUpdateDto), eq(thumbnailFile), eq(imageFile), anyString())).thenReturn(mockResponseDto);
//
//        // when, then
//        mockMvc.perform(put("/api/v1/events/" + testEventId)
//                        .param("thumbnailFile", "thumbnail.jpg")
//                        .param("imageFile", "image.jpg")
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .content(objectMapper.writeValueAsString(eventUpdateDto)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("이벤트 좋아요 API 테스트")
//    void likeEventTest() throws Exception {
//        // given
//        Long testEventId = 1L; // 테스트할 이벤트 ID
//
//        // 이벤트 서비스의 likeEvent 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.likeEvent(eq(testEventId), anyString())).thenReturn("좋아요 성공 메시지");
//
//        // when, then
//        mockMvc.perform(post("/api/v1/events/" + testEventId + "/like"))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("이벤트 참가하기 API 테스트")
//    void joinEventTest() throws Exception {
//        // given
//        Long testEventId = 1L; // 테스트할 이벤트 ID
//
//        // 이벤트 서비스의 joinEvent 메서드가 호출될 때 모의 객체가 반환할 데이터 설정
//        when(eventService.joinEvent(eq(testEventId), anyString())).thenReturn("참가 성공 메시지");
//
//        // when, then
//        mockMvc.perform(post("/api/v1/events/" + testEventId + "/join"))
//                .andExpect(status().isOk());
//    }



}