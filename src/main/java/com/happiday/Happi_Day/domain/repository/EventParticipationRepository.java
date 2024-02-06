package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventParticipation;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventParticipationRepository extends JpaRepository<EventParticipation, Long> {
    Optional<EventParticipation> findByUserAndEvent(User user, Event event);

    Page<EventParticipation> findByUser(User user, Pageable pageable);
}
