package com.happiday.Happi_Day.repository;

import com.happiday.Happi_Day.config.QueryDslConfig;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.event.QEvent;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@Import(QueryDslConfig.class)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistEventRepository artistEventRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamEventRepository teamEventRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private EventHashtagRepository eventHashtagRepository;

    @Autowired
    private ArtistSubscriptionRepository artistSubscriptionRepository;

    @Autowired
    private TeamSubscriptionRepository teamSubscriptionRepository;

    @Autowired
    EntityManager entityManager;

    private User user1;

    private Artist artist1, artist2;

    private Team team1, team2;

    private Hashtag hashtag1, hashtag2;

    private MockMultipartFile eventImage1;

    private Event event1;


    @BeforeEach
    void setUp() {

        // 사용자 생성 및 저장
        user1 = User.builder()
                .username("user1@email.com")
                .password("qwe123")
                .nickname("bigPotato")
                .realname("이용순")
                .phone("01012343214")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .artistSubscriptionList(new ArrayList<>())
                .teamSubscriptionList(new ArrayList<>())
                .build();
        userRepository.save(user1);

        artist1 = Artist.builder()
                .name("IU")
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

        MockMultipartFile eventImage1 = new MockMultipartFile(
                "multipartFile1", "eventImage1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        event1 = Event.builder()
                .title("happy happy")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl(String.valueOf(eventImage1))
                .eventHashtags(new ArrayList<>())
                .build();

        eventRepository.save(event1);

        artistEventRepository.save(new ArtistEvent(null, event1, artist1));
        artistEventRepository.save(new ArtistEvent(null, event1, artist2));
        teamEventRepository.save(new TeamEvent(null, event1, team1));
        teamEventRepository.save(new TeamEvent(null, event1, team2));
        artistSubscriptionRepository.save(new ArtistSubscription(null, user1, artist1, LocalDateTime.now()));
        artistSubscriptionRepository.save(new ArtistSubscription(null, user1, artist2, LocalDateTime.now()));
        teamSubscriptionRepository.save(new TeamSubscription(null, user1, team1, LocalDateTime.now()));
        teamSubscriptionRepository.save(new TeamSubscription(null, user1, team2, LocalDateTime.now()));


    }


    @Test
    @DisplayName("이벤트 DB 저장 테스트")
    void saveEventTest() {
        Event event = Event.builder()
                .title("happy happy title")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl("https://happi-day.s3.ap-northeast-2.amazonaws.com/default/defaultEvent.png")
                .build();

        Event savedEvent = eventRepository.save(event);

        artistEventRepository.save(new ArtistEvent(null, savedEvent, artist1));
        artistEventRepository.save(new ArtistEvent(null, savedEvent, artist2));
        teamEventRepository.save(new TeamEvent(null, savedEvent, team1));
        teamEventRepository.save(new TeamEvent(null, savedEvent, team2));
        eventHashtagRepository.save(new EventHashtag(null, savedEvent, hashtag1));
        eventHashtagRepository.save(new EventHashtag(null, savedEvent, hashtag2));

        assertNotNull(savedEvent.getId());
        assertEquals(2, artistEventRepository.findAllByEvent(savedEvent).size());
        assertEquals(2, teamEventRepository.findAllByEvent(savedEvent).size());
        assertEquals(2, eventHashtagRepository.findAllByEvent(savedEvent).size());
    }

    @Test
    @DisplayName("이벤트 단일 조회 테스트")
    void readEventTest() {
        Optional<Event> foundEvent = eventRepository.findById(event1.getId());
        assertTrue(foundEvent.isPresent());
        assertEquals(event1.getTitle(), foundEvent.get().getTitle());
    }

    @Test
    @DisplayName("이벤트 목록 조회 테스트")
    void readEventsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Event> events = eventRepository.findAll(pageable);
        assertFalse(events.isEmpty());
    }

    @Test
    @DisplayName("이벤트 삭제 테스트")
    void deleteEventTest() {
        eventRepository.delete(event1);
        Optional<Event> deletedEvent = eventRepository.findById(event1.getId());
        assertFalse(deletedEvent.isPresent());
    }

    @Test
    @DisplayName("이벤트 수정 테스트")
    void updateEventTest() {
        Event updatedEvent = eventRepository.findById(event1.getId())
                .map(existingEvent -> existingEvent.toBuilder()
                        .description("event update done")
                        .build())
                .orElseThrow();

        eventRepository.save(updatedEvent);

        Event afterUpdatedEvent = eventRepository.findById(event1.getId()).orElseThrow();
        assertEquals("event update done", afterUpdatedEvent.getDescription());
    }

    @Test
    @DisplayName("이벤트 조회수 증가 테스트")
    void eventIncreaseViewCountTest() {
        long eventId = event1.getId();
        int beforeViewCount = event1.getViewCount();

        eventRepository.increaseViewCount(eventId);
        entityManager.flush();
        entityManager.clear();

        Event updatedEvent = eventRepository.findById(eventId).orElse(null);
        assertThat(updatedEvent).isNotNull();
        assertThat(updatedEvent.getViewCount()).isEqualTo(beforeViewCount + 1);
    }
    @Test
    @DisplayName("이벤트 필터와 키워드로 조회 테스트 - title")
    void readEventByFilterAndKeywordWithTitleTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "title";
        String keyword = "happy happy";
        Page<Event> events = eventRepository.findEventsByFilterAndKeyword(pageable, filter, keyword);

        assertThat(events).isNotNull();
        assertThat(events.getContent()).extracting(Event::getTitle).contains(keyword);
    }

    @Test
    @DisplayName("이벤트 필터와 키워드로 조회 테스트 - username")
    void readEventByFilterAndKeywordWithUsernameTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "username";
        String keyword = "bigPotato";
        Page<Event> events = eventRepository.findEventsByFilterAndKeyword(pageable, filter, keyword);

        assertThat(events).isNotNull();
        assertThat(events.getContent()).isNotEmpty();
        assertThat(events.getContent()).allMatch(event -> event.getUser().getNickname().contains(keyword));
    }

    @Test
    @DisplayName("이벤트 필터와 키워드로 조회 테스트 - all")
    void readEventByFilterAndKeywordWithAllTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "all";
        String keyword = "bigPotato";
        Page<Event> events = eventRepository.findEventsByFilterAndKeyword(pageable, filter, keyword);

        assertThat(events).isNotNull();
        assertThat(events.getContent()).isNotEmpty();
        assertThat(events.getContent()).anyMatch(event ->
                event.getTitle().contains(keyword) ||
                        event.getUser().getNickname().contains(keyword) ||
                        event.getArtistsEventList().stream().anyMatch(ae -> ae.getArtist().getName().contains(keyword)) ||
                        event.getTeamsEventList().stream().anyMatch(te -> te.getTeam().getName().contains(keyword))
        );
    }

    @Test
    @DisplayName("진행 중인 이벤트 목록 조회 테스트")
    void readOngoingEventsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "";
        String keyword = "";
        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndOngoing(pageable, filter, keyword);

        assertThat(events).isNotNull();
        assertThat(events).isNotEmpty();
        assertThat(events.getContent()).allMatch(event ->
                event.getStartTime().isBefore(LocalDateTime.now()) &&
                        event.getEndTime().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("구독한 아티스트의 이벤트 목록 조회 테스트")
    void readEventsBySubscribedArtistsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "";
        String keyword = "";

        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndSubscribedArtists(pageable, filter, keyword, user1);

        assertThat(events).isNotNull();
    }

    @Test
    @DisplayName("구독한 아티스트의 진행 중인 이벤트 목록 조회 테스트")
    void readOngoingEventsBySubscribedArtistsTest() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "";
        String keyword = "";

        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndOngoingAndSubscribedArtists(pageable, filter, keyword, user1);

        assertThat(events).isNotNull();
        assertThat(events.getContent()).allMatch(event ->
                event.getStartTime().isBefore(LocalDateTime.now()) &&
                        event.getEndTime().isAfter(LocalDateTime.now()));
    }
}
