package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventHashtagRepository extends JpaRepository<EventHashtag, Long> {
    void deleteByEvent(Event event);
}
