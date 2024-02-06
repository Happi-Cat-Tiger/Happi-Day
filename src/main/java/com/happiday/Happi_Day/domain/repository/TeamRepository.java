package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByName(String teamName);

    @Query("SELECT t FROM Team t WHERE t.id NOT IN (SELECT ut.id FROM User u JOIN u.teamSubscriptionList ut WHERE u.id = :userId)")
    Page<Team> findUnsubscribedTeams(@Param("userId") Long userId, Pageable pageable);

    boolean existsByName(String name);
}
