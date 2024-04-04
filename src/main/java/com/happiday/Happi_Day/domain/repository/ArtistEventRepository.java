package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ArtistEventRepository extends JpaRepository<ArtistEvent, Long> {
    void deleteByEvent(Event event);

    List<ArtistEvent> findAllByEvent(Event savedEvent);
}
