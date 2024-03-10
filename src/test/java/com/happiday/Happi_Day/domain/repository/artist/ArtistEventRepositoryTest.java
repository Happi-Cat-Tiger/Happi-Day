package com.happiday.Happi_Day.domain.repository.artist;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArtistEventRepository;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistEventRepositoryTest {

    @Autowired
    private ArtistEventRepository artistEventRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private Event event;
    private Artist artist;
    private ArtistEvent artistEvent;

    @BeforeEach
    public void init() {
        // 유저, 이벤트, 아티스트 생성
        user = userRepository.save(User.builder()
                .username("Test Username")
                .password("Test Password")
                .nickname("Test")
                .realname("Test Realname")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build());
        artist = artistRepository.save(Artist.builder()
                .name("Test Artist 1")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        event = eventRepository.save(Event.builder()
                .user(user)
                .title("Test Title")
                .description("Test Event Description")
                .startTime(LocalDateTime.now().plusDays(10))
                .endTime(LocalDateTime.now().plusDays(10).plusHours(4))
                .location("Test Event Location")
                .address("Test Event Address")
                .build());

        // 아티스트 이벤트 관계 설정
        artistEvent = artistEventRepository.save(ArtistEvent.builder()
                .event(event)
                .artist(artist)
                .build());
    }

    @Test
    @DisplayName("이벤트로 아티스트 이벤트 삭제")
    public void deleteByEventTest() {
        // given - 위의 @BeforeEach에서 준비

        // when
        artistEventRepository.deleteByEvent(event);

        // then
        boolean exists = artistEventRepository.existsById(artistEvent.getId());
        assertThat(exists).isFalse(); // 아티스트 이벤트가 삭제되었는지 확인
    }
}
