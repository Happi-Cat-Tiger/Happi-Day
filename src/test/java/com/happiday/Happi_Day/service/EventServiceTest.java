package com.happiday.Happi_Day.service;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class EventServiceTest {

    @InjectMocks
    EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    private User user1;


    @BeforeEach
    void setup() {
// 사용자 생성 및 저장
        user1 = User.builder()
                .username("user1@email.com")
                .password("qwe123")
                .nickname("왕감자")
                .realname("이용순")
                .phone("01012343214")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();
        userRepository.save(user1);
    }

    @Test
    @DisplayName("이벤트 작성 성공")
    void createEventSuccessTest() {
        Event event = Event.builder()
                .title("제목")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl("https://happi-day.s3.ap-northeast-2.amazonaws.com/default/defaultEvent.png")
                .build();
        when(eventRepository.save(any(Event.class))).thenReturn(new Event(...)); // 저장 시 특정 Event 반환 설정

        EventResponseDto result = eventService.createEvent(createDto, null, null, "username");

        assertNotNull(result);
        verify(eventRepository).save(any(Event.class)); // 저장 메소드 호출 검증
    }

    @Test
    @DisplayName("이벤트 조회 성공")
    void readEventSuccessTest() {
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(new Event(...))); // 조회 시 특정 Event 반환 설정

        EventResponseDto result = eventService.readEvent("127.0.0.1", eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
    }

    @Test
    @DisplayName("이벤트 삭제 성공")
    void deleteEventSuccessTest() {
        Long eventId = 1L;
        doNothing().when(eventRepository).deleteById(eventId);

        assertDoesNotThrow(() -> eventService.deleteEvent(eventId, "username")); // 예외 발생하지 않음을 검증

        verify(eventRepository).deleteById(eventId); // 삭제 메소드 호출 검증
    }
}
}
