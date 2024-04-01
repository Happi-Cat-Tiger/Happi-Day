package com.happiday.Happi_Day.service;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventLike;
import com.happiday.Happi_Day.domain.entity.event.EventParticipation;
import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventUpdateDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.domain.service.EventService;
import com.happiday.Happi_Day.domain.service.RedisService;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class EventServiceTest {

    @InjectMocks
    EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    EventParticipationRepository participationRepository;

    @Mock
    EventLikeRepository likeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private ArtistRepository artistRepository;
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private FileUtils fileUtils;
    @Mock
    private DefaultImageUtils defaultImageUtils;

    @Mock
    private EventHashtagRepository eventHashtagRepository;

    @Mock
    ArtistEventRepository artistEventRepository;

    @Mock
    TeamEventRepository teamEventRepository;

    @Mock
    RedisService redisService;


    private User user1;

    private Artist artist1, artist2;

    private Team team1, team2;

    private Hashtag hashtag1, hashtag2;

    private MockMultipartFile eventImage1, eventImage2;

    private Event event1;


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
        userRepository.save(user1);

        artist1 = Artist.builder()
                .name("아이유")
                .description("아이유입니다.")
                .build();
        artistRepository.save(artist1);

        artist2 = Artist.builder()
                .name("김범수")
                .description("김범수입니다.")
                .build();
        artistRepository.save(artist2);


        team1 = Team.builder()
                .name("동방신기")
                .description("동방신기입니다.")
                .build();
        teamRepository.save(team1);

        team2 = Team.builder()
                .name("소녀시대")
                .description("소녀시대입니다.")
                .build();
        teamRepository.save(team2);
        hashtag1 = Hashtag.builder()
                .tag("동방신기포에버")
                .build();
        hashtagRepository.save(hashtag1);

        hashtag2 = Hashtag.builder()
                .tag("소녀시대포에버")
                .build();
        hashtagRepository.save(hashtag2);

        eventImage1 = new MockMultipartFile(
                "multipartFile1", "eventImage1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        eventImage2 = new MockMultipartFile(
                "multipartFile2", "eventImage2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        event1 = Event.builder()
                .title("제목")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl(String.valueOf(eventImage1))
                .eventHashtags(new ArrayList<>())
                .build();

//        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event1));

//        event1 = mock(Event.class);
//        when(event1.getTitle()).thenReturn("제목");
//        when(event1.getUser()).thenReturn(user1);
//        when(event1.getStartTime()).thenReturn(LocalDateTime.now().minusMonths(1));
//        when(event1.getEndTime()).thenReturn(LocalDateTime.now().plusMonths(3));
//        when(event1.getDescription()).thenReturn("내용");
//        when(event1.getAddress()).thenReturn("서울특별시 서초구 반포대로30길 32");
//        when(event1.getLocation()).thenReturn("1층 카페 이로");
//        when(event1.getImageUrl()).thenReturn(String.valueOf(eventImage1));


//        when(event1.getEventHashtags()).thenReturn(new ArrayList<>());

//        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
//        when(artistRepository.findByName(anyString())).thenReturn(Optional.of(artist1));
//
//        when(eventRepository.save(any(Event.class))).thenReturn(event1); // 이벤트 저장 시 mockEvent 반환

    }

    @Test
    @DisplayName("이벤트 작성 성공")
    void createEventSuccessTest() {
        List<String> hashtagRequestList = Arrays.asList("해시태그1", "소녀시대", "아이유");

        EventCreateDto createDto = EventCreateDto.builder()
                .title("제목1")
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .hashtags(hashtagRequestList)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(artistRepository.findByName(anyString())).thenReturn(Optional.of(artist1));

        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventResponseDto result = eventService.createEvent(createDto, eventImage2, eventImage1, user1.getUsername());

        assertNotNull(result);
        assertEquals("제목1", result.getTitle());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("기본 썸네일 이벤트 작성 성공")
    void createDefaultThumbnailEventSuccessTest() {
        List<String> hashtagRequestList = Arrays.asList("해시태그1", "소녀시대", "아이유");

        EventCreateDto createDto = EventCreateDto.builder()
                .title("제목1")
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .hashtags(hashtagRequestList)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(artistRepository.findByName(anyString())).thenReturn(Optional.of(artist1));

        when(eventRepository.save(any(Event.class))).thenReturn(event1);

        EventResponseDto result = eventService.createEvent(createDto, null, eventImage1, user1.getUsername());

        assertNotNull(result);
        assertEquals("제목1", result.getTitle());
        assertEquals(result.getThumbnailUrl(), defaultImageUtils.getDefaultImageUrlEventThumbnail());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("이벤트 리스트 조회")
    void readEventsTest() {
        int totalEvents = 15;

        Pageable pageable = PageRequest.of(0, 10);
        String filter = "testFilter";
        String keyword = "testKeyword";

        User mockUser = mock(User.class);

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= totalEvents; i++) {
            Event mockEvent = mock(Event.class);
            when(mockEvent.getUser()).thenReturn(mockUser);
            eventList.add(mockEvent);
        }

        Page<Event> pageOfEvents = new PageImpl<>(eventList, pageable, eventList.size());

        when(eventRepository.findEventsByFilterAndKeyword(pageable, filter, keyword)).thenReturn(pageOfEvents);

        Page<EventListResponseDto> results = eventService.readEvents(pageable, filter, keyword);

        assertNotNull(results);
        assertEquals(15, results.getContent().size());
        verify(eventRepository).findEventsByFilterAndKeyword(pageable, filter, keyword);
    }

    @Test
    @DisplayName("페이지별 이벤트 목록 조회")
    void readEventsPerPageTest() {
        int totalEvents = 12;

        Pageable firstPageable = PageRequest.of(0, 10);
        Pageable secondPageable = PageRequest.of(1, 10);

        String filter = "testFilter";
        String keyword = "testKeyword";

        User mockUser = mock(User.class);

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= totalEvents; i++) {
            Event mockEvent = mock(Event.class);
            when(mockEvent.getUser()).thenReturn(mockUser);
            eventList.add(mockEvent);
        }

        Page<Event> firstPageOfEvents = new PageImpl<>(eventList.subList(0, 10), firstPageable, totalEvents);
        when(eventRepository.findEventsByFilterAndKeyword(firstPageable, filter, keyword)).thenReturn(firstPageOfEvents);

        Page<Event> secondPageOfEvents = new PageImpl<>(eventList.subList(10, 12), secondPageable, totalEvents);
        when(eventRepository.findEventsByFilterAndKeyword(secondPageable, filter, keyword)).thenReturn(secondPageOfEvents);

        Page<EventListResponseDto> firstPageResults = eventService.readEvents(firstPageable, filter, keyword);
        assertEquals(10, firstPageResults.getContent().size());

        Page<EventListResponseDto> secondPageResults = eventService.readEvents(secondPageable, filter, keyword);
        assertEquals(2, secondPageResults.getContent().size());

        verify(eventRepository).findEventsByFilterAndKeyword(firstPageable, filter, keyword);
        verify(eventRepository).findEventsByFilterAndKeyword(secondPageable, filter, keyword);
    }

    @Test
    @DisplayName("진행중인 이벤트 리스트 조회")
    void readOngoingEventsTest() {
        int totalEvents = 12;

        PageRequest pageable = PageRequest.of(0, 10);
        String filter = "someFilter";
        String keyword = "someKeyword";
        User mockUser = mock(User.class);

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= totalEvents; i++) {
            Event mockEvent = mock(Event.class);
            when(mockEvent.getUser()).thenReturn(mockUser);
            eventList.add(mockEvent);
        }

        Page<Event> eventPage = new PageImpl<>(eventList, pageable, eventList.size());

        when(eventRepository.findEventsByFilterAndKeywordAndOngoing(pageable, filter, keyword)).thenReturn(eventPage);

        assertNotNull(eventService.readOngoingEvents(pageable, filter, keyword));
    }

    @Test
    @DisplayName("구독한 아티스트의 이벤트 리스트 조회")
    void readEventsBySubscribedArtistsTest() {
        int totalEvents = 12;

        PageRequest pageable = PageRequest.of(0, 10);
        String filter = "someFilter";
        String keyword = "someKeyword";

        User mockUser = mock(User.class);

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= totalEvents; i++) {
            Event mockEvent = mock(Event.class);
            when(mockEvent.getUser()).thenReturn(mockUser);
            eventList.add(mockEvent);
        }

        Page<Event> eventPage = new PageImpl<>(eventList, pageable, eventList.size());

        when(userRepository.findByUsername(user1.getUsername())).thenReturn(java.util.Optional.of(user1));
        when(eventRepository.findEventsByFilterAndKeywordAndSubscribedArtists(pageable, filter, keyword, user1)).thenReturn(eventPage);

        assertNotNull(eventService.readEventsBySubscribedArtists(pageable, filter, keyword, user1.getUsername()));
    }

    @Test
    @DisplayName("구독한 아티스트의 진행중인 이벤트 리스트 조회")
    void readOngoingEventsBySubscribedArtistsTest() {
        int totalEvents = 12;

        PageRequest pageable = PageRequest.of(0, 10);
        String filter = "someFilter";
        String keyword = "someKeyword";
        User mockUser = mock(User.class);

        List<Event> eventList = new ArrayList<>();

        for (int i = 1; i <= totalEvents; i++) {
            Event mockEvent = mock(Event.class);
            when(mockEvent.getUser()).thenReturn(mockUser);
            eventList.add(mockEvent);
        }

        Page<Event> eventPage = new PageImpl<>(eventList, pageable, eventList.size());

        when(userRepository.findByUsername(user1.getUsername())).thenReturn(java.util.Optional.of(user1));
        when(eventRepository.findEventsByFilterAndKeywordAndOngoingAndSubscribedArtists(pageable, filter, keyword, user1)).thenReturn(eventPage);

        assertNotNull(eventService.readOngoingEventsBySubscribedArtists(pageable, filter, keyword, user1.getUsername()));
    }

    @Test
    @DisplayName("이벤트 상세 조회")
    void readEventSuccessTest() {
        String clientAddress = "0:0:0:0:0:0:0:1";
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(redisService.isFirstIpRequest(clientAddress, eventId)).thenReturn(true);

        EventResponseDto result = eventService.readEvent(clientAddress, eventId);

        assertNotNull(result);
        assertEquals(event1.getTitle(), result.getTitle());
        verify(eventRepository).findById(eventId);
        verify(redisService).isFirstIpRequest(clientAddress, eventId);
        verify(redisService).clientRequest(clientAddress, eventId);
    }

    @Test
    @DisplayName("이벤트 수정 성공")
    void updateEventSuccessTest() {
        Long eventId = event1.getId();
        EventUpdateDto updateDto = EventUpdateDto.builder()
                .title("수정된 제목")
                .description("수정된 내용")
                .build();

        MockMultipartFile updatedThumbnail = new MockMultipartFile("updatedThumbnail", "updatedThumbnail.jpg", MediaType.IMAGE_JPEG_VALUE, "updatedThumbnailData".getBytes());
        MockMultipartFile updatedImage = new MockMultipartFile("updatedImage", "updatedImage.jpg", MediaType.IMAGE_JPEG_VALUE, "updatedImageData".getBytes());

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        EventResponseDto result = eventService.updateEvent(eventId, updateDto, updatedThumbnail, updatedImage, user1.getUsername());

        assertNotNull(result);
        assertEquals(updateDto.getTitle(), result.getTitle());
        verify(eventRepository).save(any(Event.class));
    }
    @Test
    @DisplayName("이벤트 삭제 성공")
    void deleteEventSuccessTest() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        eventService.deleteEvent(eventId, user1.getUsername());

        verify(eventRepository).delete(event1);
    }

    @Test
    @DisplayName("이벤트 좋아요 성공")
    void likeEventSuccessTest() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(likeRepository.findByUserAndEvent(user1, event1)).thenReturn(Optional.empty());

        String response = eventService.likeEvent(eventId, user1.getUsername());

        assertTrue(response.contains("좋아요 성공"));
        verify(likeRepository).save(any(EventLike.class));
    }

    @Test
    @DisplayName("이벤트 좋아요 취소 성공")
    void cancelLikeEventTest() {
        Long eventId = 1L;
        EventLike existingLike = EventLike.builder().user(user1).event(event1).build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(likeRepository.findByUserAndEvent(user1, event1)).thenReturn(Optional.of(existingLike));

        String response = eventService.likeEvent(eventId, user1.getUsername());

        assertTrue(response.contains("좋아요 취소"));
        verify(likeRepository).delete(existingLike);
    }

    @Test
    @DisplayName("이벤트 참여 성공")
    void joinEventSuccessTest() {
        Long eventId = 1L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(participationRepository.findByUserAndEvent(user1, event1)).thenReturn(Optional.empty());

        String response = eventService.joinEvent(eventId, user1.getUsername());

        assertTrue(response.contains("이벤트 참가"));
        verify(participationRepository).save(any(EventParticipation.class));
    }

    @Test
    @DisplayName("이벤트 참여 취소 성공")
    void cancelJoinEventTest() {
        Long eventId = 1L;
        EventParticipation existingParticipation = EventParticipation.builder().user(user1).event(event1).build();

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event1));
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));
        when(participationRepository.findByUserAndEvent(user1, event1)).thenReturn(Optional.of(existingParticipation));

        String response = eventService.joinEvent(eventId, user1.getUsername());

        assertTrue(response.contains("이벤트 참가 취소"));
        verify(participationRepository).delete(existingParticipation);
    }

    @Test
    @DisplayName("이벤트 조회수 증가 성공 테스트")

    public void increaseViewCountTest1() {
        // ArgumentCaptor 인스턴스 생성
        ArgumentCaptor<Long> eventIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> clientAddressCaptor = ArgumentCaptor.forClass(String.class);

        // 테스트 실행
        eventService.increaseViewCount("127.0.0.1", 1L);

        // verify를 사용해 메서드가 호출되었는지 확인하고, ArgumentCaptor로 파라미터 값을 캡처
        verify(eventRepository).increaseViewCount(eventIdCaptor.capture());
        verify(redisService).clientRequest(clientAddressCaptor.capture(), eventIdCaptor.capture());

        // 캡처된 파라미터 값이 예상한 값과 일치하는지 검증
        assertEquals(Long.valueOf(1L), eventIdCaptor.getValue());
        assertEquals("127.0.0.1", clientAddressCaptor.getValue());
    }

    @Test
    @DisplayName("이벤트 조회수 증가 성공")
    void increaseViewCountTest() {

        Long eventId = 1L;
        String clientAddress = "127.0.0.1";

        when(eventRepository.increaseViewCount(anyLong())).thenReturn(2); // 예: 업데이트된 행의 수가 1이라고 가정
        doNothing().when(redisService).clientRequest(clientAddress, eventId);

        eventService.increaseViewCount(clientAddress, eventId);

        verify(eventRepository).increaseViewCount(eventId);
        verify(redisService).clientRequest(clientAddress, eventId);
    }



}

