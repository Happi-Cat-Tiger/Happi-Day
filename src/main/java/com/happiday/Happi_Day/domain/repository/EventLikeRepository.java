package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventLike;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventLikeRepository extends JpaRepository<EventLike, Long> {
    Optional<EventLike> findByUserAndEvent(User user, Event event);

    long countByEvent(Event event);

    Page<EventLike> findByUser(User user, Pageable pageable);
}
