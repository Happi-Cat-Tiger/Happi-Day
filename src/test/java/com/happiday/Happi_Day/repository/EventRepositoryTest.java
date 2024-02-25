package com.happiday.Happi_Day.repository;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@DataJpaTest
public class EventRepositoryTest {

    @Autowired
    private EventRepository eventRepository;

    @Test
    @DisplayName("이벤트 생성")

    void addEvent() {
        Event event = Event.builder()
                .title("이벤트 제목")
                .user()
                .startTime(LocalDateTime.parse("2023-08-31T10:00:00"))
                .endTime(LocalDateTime.parse("2024-08-31T10:00:00"))
                .description("이벤트의 내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .artists()
                .teams()
                .eventHashtags()
                .imageUrl()
                .thumbnailUrl()
                .build();
    }
}
