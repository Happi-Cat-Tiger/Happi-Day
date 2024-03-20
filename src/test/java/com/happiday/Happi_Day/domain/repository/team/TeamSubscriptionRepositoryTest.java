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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamSubscriptionRepositoryTest {

    @Autowired
    private TeamSubscriptionRepository teamSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    private User user;
    private Team team;
    private TeamSubscription teamSubscription;

    @BeforeEach
    public void init() {
        // 사용자와 팀 엔티티 준비
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
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());

        // 사용자와 팀 구독 관계 설정
        teamSubscription = TeamSubscription.builder()
                .user(user)
                .team(team)
                .build();
        teamSubscriptionRepository.save(teamSubscription);
    }

    @Test
    @DisplayName("사용자와 팀으로 구독 존재 여부 확인")
    public void existsByUserAndTeamTest() {
        // when
        boolean exists = teamSubscriptionRepository.existsByUserAndTeam(user, team);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("사용자와 팀으로 구독 정보 조회")
    public void findByUserAndTeamTest() {
        // when
        Optional<TeamSubscription> foundSubscription = teamSubscriptionRepository.findByUserAndTeam(user, team);

        // then
        assertThat(foundSubscription.isPresent()).isTrue();
        assertThat(foundSubscription.get()).isEqualTo(teamSubscription);
    }
}
