package com.happiday.Happi_Day.domain.service;

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
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventReviewService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventReviewRepository reviewRepository;
    private final ReviewImageRepository imageRepository;
    private final FileUtils fileUtils;

    @Transactional
    public EventReviewResponseDto createReview(Long eventId, EventReviewCreateDto request, List<MultipartFile> imageFiles, String username) {
        log.info("컨트롤러에서 넘어온 이미지 파일 : {}", imageFiles.size());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // 진행되지 않은 이벤트인지 검증
        boolean isEventStarted = event.getStartTime().isBefore(LocalDateTime.now());

        if (!isEventStarted) {
            throw new CustomException(ErrorCode.EVENT_NOT_STARTED);
        }

        // 해당 이벤트에 리뷰를 이미 작성했는지 검증
        boolean isAlreadyWritten = user.getEventReviews().stream()
                .anyMatch(review -> review.getEvent().equals(event));

        if (isAlreadyWritten) {
            throw new CustomException(ErrorCode.EVENT_REVIEW_ALREADY_SUBMITTED);
        }


        EventReview review = EventReview.builder()
                .user(user)
                .event(event)
                .description(request.getDescription())
                .rating(request.getRating())
                .build();

        List<ReviewImage> reviewImages = new ArrayList<>();

        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> imageUrls = fileUtils.uploadFiles(imageFiles);

            for (String imgUrl : imageUrls) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imgUrl)
                        .eventReview(review)
                        .build();
                reviewImages.add(reviewImage);
            }
        }

        review.setImages(reviewImages);

        reviewRepository.save(review);
        return EventReviewResponseDto.fromEntity(review);

    }

    public Page<EventReviewListResponseDto> readReviews(Long eventId, Pageable pageable) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        Page<EventReview> reviewList = reviewRepository.findAllByEvent(event, pageable);

        return reviewList.map(EventReviewListResponseDto::fromEntity);
    }

    public EventReviewResponseDto readReview(Long eventId, Long reviewId) {
        log.info("이벤트 리뷰 단일 조회");

        eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        EventReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_REVIEW_NOT_FOUND));

        return EventReviewResponseDto.fromEntity(review);
    }

    @Transactional
    public EventReviewResponseDto updateReview (
            Long eventId, Long reviewId, EventReviewUpdateDto request, String username, List<MultipartFile> imageFiles
    ) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        EventReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_REVIEW_NOT_FOUND));

        if (!review.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (imageFiles != null && !imageFiles.isEmpty()) {
            log.info("이미지 있음 ");
            imageRepository.deleteAll(review.getImages());
//            review.getImages().clear();

            List<String> imageUrls = fileUtils.uploadFiles(imageFiles);
            List<ReviewImage> reviewImages = imageUrls.stream()
                    .map(imgUrl -> ReviewImage.builder()
                            .imageUrl(imgUrl)
                            .eventReview(review)
                            .build())
                    .collect(Collectors.toList());
            review.setImages(reviewImages);

        }

        review.update(request.toEntity());
        reviewRepository.save(review);

        return EventReviewResponseDto.fromEntity(review);
    }

    @Transactional
    public void deleteReview(Long eventId, Long reviewId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        EventReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_REVIEW_NOT_FOUND));

        if (!review.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        reviewRepository.delete(review);

    }


}
