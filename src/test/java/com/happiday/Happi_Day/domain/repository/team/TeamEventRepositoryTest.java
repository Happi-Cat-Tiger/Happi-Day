package com.happiday.Happi_Day.domain.repository.team;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.TeamEventRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamEventRepositoryTest {

    @Autowired
    private TeamEventRepository teamEventRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    private Event event;
    private Team team;
    private TeamEvent teamEvent;
    private User user;

    @BeforeEach
    public void init() {
        // 이벤트와 팀 엔티티 준비
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
        event = eventRepository.save(Event.builder()
                .user(user)
                .title("Test Event")
                .description("Test Description")
                .startTime(LocalDateTime.now().plusDays(10))
                .endTime(LocalDateTime.now().plusDays(10).plusHours(4))
                .location("Test Location")
                .address("Test Address")
                .thumbnailUrl("http://example.com/thumbnail.jpg")
                .build());
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());

        // 팀 이벤트 관계 설정
        teamEvent = TeamEvent.builder()
                .event(event)
                .team(team)
                .build();
        teamEventRepository.save(teamEvent);
    }

    @Test
    @DisplayName("이벤트로 팀 이벤트 삭제")
    public void deleteByEventTest() {
        // given - 위의 @BeforeEach에서 준비된 데이터

        // when
        teamEventRepository.deleteByEvent(event);

        // then
        boolean exists = teamEventRepository.existsById(teamEvent.getId());
        assertThat(exists).isFalse(); // 팀 이벤트가 삭제되었는지 확인
    }
}
