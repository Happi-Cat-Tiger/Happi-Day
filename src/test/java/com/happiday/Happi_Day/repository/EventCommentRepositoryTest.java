package com.happiday.Happi_Day.repository;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventComment;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class EventCommentRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventCommentRepository eventCommentRepository;

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

    private EventComment comment1;


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

        comment1 = EventComment.builder()
                .user(user1)
                .event(event1)
                .content("eventComment test content 1")
                .build();
        EventComment comment2 = EventComment.builder()
                .user(user1)
                .event(event1)
                .content("eventComment test content 2")
                .build();
        eventCommentRepository.save(comment1);
        eventCommentRepository.save(comment2);

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
    @DisplayName("댓글 작성 테스트")
    void createCommentTest() {
        EventComment comment = eventCommentRepository.save(new EventComment(null, user1, event1, "new comment"));

        assertNotNull(comment.getId());
        assertEquals("new comment", comment.getContent());
        assertEquals(user1.getId(), comment.getUser().getId());
        assertEquals(event1.getId(), comment.getEvent().getId());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        EventComment updatedComment = eventCommentRepository.findById(comment1.getId())
                .map(existingComment -> existingComment.toBuilder()
                        .content("comment update done")
                        .build())
                .orElseThrow();

        eventCommentRepository.save(updatedComment);

        EventComment afterUpdatedComment = eventCommentRepository.findById(comment1.getId()).orElseThrow();
        assertEquals("comment update done", afterUpdatedComment.getContent());
    }
    @Test
    @DisplayName("해당 이벤트 댓글 목록 조회 테스트")
    void readCommentsByEventTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<EventComment> eventCommentList = eventCommentRepository.findAllByEvent(event1, pageable);

        assertTrue(eventCommentList.stream()
                .anyMatch(comment -> "eventComment test content 1".equals(comment.getContent())));

    }

    @Test
    @DisplayName("해당 댓글 삭제 테스트")
    void deleteCommentTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<EventComment> eventCommentList = eventCommentRepository.findAllByEvent(event1, pageable);
        assertTrue(eventCommentList.getContent().size() > 0);

        eventCommentRepository.delete(comment1);
        entityManager.flush();

        Page<EventComment> afterEventCommentList = eventCommentRepository.findAllByEvent(event1, pageable);


        assertFalse(afterEventCommentList.stream()
                .anyMatch(comment -> "eventComment test content 1".equals(comment.getContent())));
    }
}
