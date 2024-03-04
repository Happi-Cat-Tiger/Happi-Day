package com.happiday.Happi_Day.repository;

import com.happiday.Happi_Day.config.QueryDslConfig;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


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

    private User user1;

    private Artist artist1, artist2;

    private Team team1, team2;


    @BeforeEach
    void setUp() {

        // 사용자 생성 및 저장
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
    }


    @Test
    @DisplayName("이벤트 DB에 저장이 잘 되는지 확인")
    void saveEvent() {
        Hashtag hashtag1 = Hashtag.builder()
                .tag("동방신기포에버")
                .build();
        hashtagRepository.save(hashtag1);

        Hashtag hashtag2 = Hashtag.builder()
                .tag("소녀시대포에버")
                .build();
        hashtagRepository.save(hashtag2);

        Event event = Event.builder()
                .title("제목")
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
}
