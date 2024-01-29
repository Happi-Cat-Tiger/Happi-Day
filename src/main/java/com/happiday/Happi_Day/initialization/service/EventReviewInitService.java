package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.EventReviewRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventReviewInitService {
    private final EventReviewRepository eventReviewRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public void initEventReviews() {
        User writer1 = userRepository.findById(1L).orElse(null);
        User writer2 = userRepository.findById(2L).orElse(null);
        Event sampleEvent1 = eventRepository.findById(1L).orElse(null);
        Event sampleEvent2 = eventRepository.findById(2L).orElse(null);

        List<EventReview> reviews = List.of(
                EventReview.builder()
                        .user(writer1)
                        .event(sampleEvent1)
                        .description("너무 재밌었어요!")
                        .rating(5)
                        .build(),
                EventReview.builder()
                        .user(writer1)
                        .event(sampleEvent2)
                        .description("꽤 재밌었어요!!!")
                        .rating(4)
                        .build(),
                EventReview.builder()
                        .user(writer2)
                        .event(sampleEvent2)
                        .description("별로였어요")
                        .rating(2)
                        .build()
        );

        try {
            eventReviewRepository.saveAll(reviews);
        } catch (Exception e) {
            log.error("DB Seeder 이벤트 리뷰 저장 중 예외 발생", e);
            throw new CustomException(ErrorCode.DB_SEEDER_EVENT_COMMENT_SAVE_ERROR);
        }
    }
}
