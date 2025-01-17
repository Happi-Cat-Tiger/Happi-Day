package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TeamEventRepository extends JpaRepository<TeamEvent, Long> {
    void deleteByEvent(Event event);

    List<TeamEvent> findAllByEvent(Event savedEvent);
}
