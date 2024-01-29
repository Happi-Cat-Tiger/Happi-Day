package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventReviewRepository extends JpaRepository<EventReview, Long> {
    Page<EventReview> findAllByEvent(Event event, Pageable pageable);

    Page<EventReview> findAllByUser(User user, Pageable pageable);}
