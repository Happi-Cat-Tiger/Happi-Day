package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamSubscriptionRepository extends JpaRepository<TeamSubscription, Long> {
    boolean existsByUserAndTeam(User user, Team team);
    Optional<TeamSubscription> findByUserAndTeam(User user, Team team);
}
