package com.happiday.Happi_Day.service;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.event.ReviewImage;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewUpdateDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.EventReviewRepository;
import com.happiday.Happi_Day.domain.repository.ReviewImageRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.EventReviewService;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Transactional
class EventReviewServiceTest {

    @InjectMocks
    private EventReviewService reviewService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventReviewRepository reviewRepository;

    @Mock
    private ReviewImageRepository imageRepository;

    @Mock
    private FileUtils fileUtils;

    private User user;
    private Event event;
    private EventReview review;
    private List<MultipartFile> imageFiles;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .username("testuser")
                .eventReviews(new ArrayList<>())  // Initialize eventReviews list
                .build();

        event = Event.builder()
                .title("Test Event")
                .startTime(LocalDateTime.now().minusDays(1))
                .endTime(LocalDateTime.now().plusDays(1))
                .build();

        review = EventReview.builder()
                .user(user)
                .event(event)
                .description("Great event")
                .rating(5)
                .images(new ArrayList<>())  // Initialize images list
                .build();

        imageFiles = List.of(
                new MockMultipartFile("file1", "image1.jpg", "image/jpeg", "image data".getBytes()),
                new MockMultipartFile("file2", "image2.jpg", "image/jpeg", "image data".getBytes())
        );
    }

    @Test
    @DisplayName("리뷰 생성 성공")
    void createReviewSuccessTest() {
        EventReviewCreateDto createDto = EventReviewCreateDto.builder()
                .description("Awesome event")
                .rating(5)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(fileUtils.uploadFiles(anyList())).thenReturn(List.of("url1", "url2"));
        when(reviewRepository.save(any(EventReview.class))).thenReturn(review);

        EventReviewResponseDto result = reviewService.createReview(1L, createDto, imageFiles, "testuser");

        assertNotNull(result);
        assertEquals("Awesome event", result.getDescription());
        verify(reviewRepository).save(any(EventReview.class));
        verify(imageRepository, times(2)).save(any(ReviewImage.class));
    }

    @Test
    @DisplayName("리뷰 생성 시 사용자가 존재하지 않는 경우")
    void createReviewWithNoExistUserTest() {
        EventReviewCreateDto createDto = EventReviewCreateDto.builder()
                .description("Awesome event")
                .rating(5)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.createReview(1L, createDto, imageFiles, "noExistUser");
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 생성 시 이벤트가 존재하지 않는 경우")
    void createReviewWithNoExistEventTest() {
        EventReviewCreateDto createDto = EventReviewCreateDto.builder()
                .description("Awesome event")
                .rating(5)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.createReview(1L, createDto, imageFiles, "testuser");
        });

        assertEquals(ErrorCode.EVENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 생성 시 이벤트가 시작되지 않은 경우")
    void createReviewWithEventNotStartedTest() {
        Event futureEvent = Event.builder()
                .title("Future Event")
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .build();

        EventReviewCreateDto createDto = EventReviewCreateDto.builder()
                .description("Awesome event")
                .rating(5)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(futureEvent));

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.createReview(1L, createDto, imageFiles, "testuser");
        });

        assertEquals(ErrorCode.EVENT_NOT_STARTED, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 생성 시 이미 리뷰를 작성한 경우")
    void createReviewWithAlreadySubmittedReviewTest() {
        user.getEventReviews().add(review);

        EventReviewCreateDto createDto = EventReviewCreateDto.builder()
                .description("Awesome event")
                .rating(5)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.createReview(1L, createDto, imageFiles, "testuser");
        });

        assertEquals(ErrorCode.EVENT_REVIEW_ALREADY_SUBMITTED, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 목록 조회 성공")
    void readReviewsSuccessTest() {
        Pageable pageable = PageRequest.of(0, 10);
        List<EventReview> reviewList = List.of(review);
        Page<EventReview> reviewPage = new PageImpl<>(reviewList, pageable, reviewList.size());

        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findAllByEvent(any(Event.class), any(Pageable.class))).thenReturn(reviewPage);

        Page<EventReviewListResponseDto> result = reviewService.readReviews(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Great event", result.getContent().get(0).getDescription());
    }

    @Test
    @DisplayName("리뷰 단일 조회 성공")
    void readReviewSuccessTest() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        EventReviewResponseDto result = reviewService.readReview(1L, 1L);

        assertNotNull(result);
        assertEquals("Great event", result.getDescription());
    }

    @Test
    @DisplayName("리뷰 단일 조회 시 이벤트가 존재하지 않는 경우")
    void readReviewWithNoExistEventTest() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.readReview(1L, 1L);
        });

        assertEquals(ErrorCode.EVENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 단일 조회 시 리뷰가 존재하지 않는 경우")
    void readReviewWithNoExistReviewTest() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.readReview(1L, 1L);
        });

        assertEquals(ErrorCode.EVENT_REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void updateReviewSuccessTest() {
        EventReviewUpdateDto updateDto = EventReviewUpdateDto.builder()
                .description("Updated description")
                .rating(4)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));
        when(fileUtils.uploadFiles(anyList())).thenReturn(List.of("url1", "url2"));

        EventReviewResponseDto result = reviewService.updateReview(1L, 1L, updateDto, "testuser", imageFiles);

        assertNotNull(result);
        assertEquals("Updated description", result.getDescription());
        assertEquals(4, result.getRating());
        verify(reviewRepository).save(any(EventReview.class));
        verify(imageRepository, times(2)).save(any(ReviewImage.class));
    }

    @Test
    @DisplayName("리뷰 수정 시 사용자가 존재하지 않는 경우")
    void updateReviewWithNoExistUserTest() {
        EventReviewUpdateDto updateDto = EventReviewUpdateDto.builder()
                .description("Updated description")
                .rating(4)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.updateReview(1L, 1L, updateDto, "noExistUser", imageFiles);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 수정 시 이벤트가 존재하지 않는 경우")
    void updateReviewWithNoExistEventTest() {
        EventReviewUpdateDto updateDto = EventReviewUpdateDto.builder()
                .description("Updated description")
                .rating(4)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.updateReview(1L, 1L, updateDto, "testuser", imageFiles);
        });

        assertEquals(ErrorCode.EVENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 수정 시 리뷰가 존재하지 않는 경우")
    void updateReviewWithNoExistReviewTest() {
        EventReviewUpdateDto updateDto = EventReviewUpdateDto.builder()
                .description("Updated description")
                .rating(4)
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.updateReview(1L, 1L, updateDto, "testuser", imageFiles);
        });

        assertEquals(ErrorCode.EVENT_REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 수정 시 사용자가 작성자가 아닌 경우")
    void updateReviewWithDifferentUserTest() {
        EventReviewUpdateDto updateDto = EventReviewUpdateDto.builder()
                .description("Updated description")
                .rating(4)
                .build();

        User differentUser = User.builder()
                .username("differentUser")
                .eventReviews(new ArrayList<>())  // Initialize eventReviews list
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(differentUser));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.updateReview(1L, 1L, updateDto, "differentUser", imageFiles);
        });

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void deleteReviewSuccessTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        reviewService.deleteReview(1L, 1L, "testuser");

        verify(reviewRepository).delete(any(EventReview.class));
    }

    @Test
    @DisplayName("리뷰 삭제 시 사용자가 존재하지 않는 경우")
    void deleteReviewWithNoExistUserTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.deleteReview(1L, 1L, "noExistUser");
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 삭제 시 이벤트가 존재하지 않는 경우")
    void deleteReviewWithNoExistEventTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.deleteReview(1L, 1L, "testuser");
        });

        assertEquals(ErrorCode.EVENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 삭제 시 리뷰가 존재하지 않는 경우")
    void deleteReviewWithNoExistReviewTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.deleteReview(1L, 1L, "testuser");
        });

        assertEquals(ErrorCode.EVENT_REVIEW_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("리뷰 삭제 시 사용자가 작성자가 아닌 경우")
    void deleteReviewWithDifferentUserTest() {
        User differentUser = User.builder()
                .username("differentUser")
                .eventReviews(new ArrayList<>())  // Initialize eventReviews list
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(differentUser));
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));
        when(reviewRepository.findById(anyLong())).thenReturn(Optional.of(review));

        CustomException exception = assertThrows(CustomException.class, () -> {
            reviewService.deleteReview(1L, 1L, "differentUser");
        });

        assertEquals(ErrorCode.FORBIDDEN, exception.getErrorCode());
    }
}

