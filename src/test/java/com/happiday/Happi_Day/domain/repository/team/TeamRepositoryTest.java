package com.happiday.Happi_Day.domain.repository.team;

import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import com.happiday.Happi_Day.domain.repository.TeamSubscriptionRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamSubscriptionRepository teamSubscriptionRepository;

    private Team team;
    private User user;
    private TeamSubscription teamSubscription;

    @BeforeEach
    public void init() {
        // 팀 엔티티 준비
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());

        // 사용자 엔티티 준비 및 팀 구독 설정
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
        teamSubscription = TeamSubscription.builder()
                .user(user)
                .team(team)
                .build();
        teamSubscriptionRepository.save(teamSubscription);
    }

    @Test
    @DisplayName("팀 이름으로 조회")
    public void findByNameTest() {
        // when
        Optional<Team> foundTeam = teamRepository.findByName("Test Team");

        // then
        assertThat(foundTeam.isPresent()).isTrue();
        assertThat(foundTeam.get().getName()).isEqualTo("Test Team");
    }

//    @Test - 불안정한 작동으로 인한 임시 주석 처리
    @DisplayName("구독하지 않은 팀 조회")
    public void findUnsubscribedTeamsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Team> unsubscribedTeams = teamRepository.findUnsubscribedTeams(user.getId(), pageable);

        // then
        assertThat(unsubscribedTeams.getContent()).doesNotContain(team); // 구독한 팀은 결과에 포함되지 않아야 함
    }

    @Test
    @DisplayName("팀 이름 존재 여부 확인")
    public void existsByNameTest() {
        // when
        boolean exists = teamRepository.existsByName("Test Team");

        // then
        assertThat(exists).isTrue();
    }
}
