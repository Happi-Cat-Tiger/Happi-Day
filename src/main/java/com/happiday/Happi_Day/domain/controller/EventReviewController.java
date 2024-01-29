package com.happiday.Happi_Day.domain.controller;


import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewUpdateDto;
import com.happiday.Happi_Day.domain.service.EventReviewService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events/{eventId}/reviews")
@RequiredArgsConstructor
@Slf4j
public class EventReviewController {
    private final EventReviewService reviewService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventReviewResponseDto> createReview(
            @PathVariable("eventId") Long eventId,
            @Valid @RequestPart(value = "review") EventReviewCreateDto reviewCreateDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
            ){
        String username = SecurityUtils.getCurrentUsername();
        EventReviewResponseDto responseDto = reviewService.createReview(eventId, reviewCreateDto, imageFiles, username);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<EventReviewListResponseDto>> readReviews(
            @PathVariable("eventId") Long eventId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<EventReviewListResponseDto> responseDtoList = reviewService.readReviews(eventId, pageable);
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<EventReviewResponseDto> readReview(@PathVariable("eventId") Long eventId, @PathVariable("reviewId") Long reviewId) {

        EventReviewResponseDto responseDto = reviewService.readReview(eventId, reviewId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<EventReviewResponseDto> updateReview(
            @PathVariable("eventId") Long eventId,
            @PathVariable("reviewId") Long reviewId,
            @RequestPart(value = "review") EventReviewUpdateDto reviewUpdateDto,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles
            ) {
        String username = SecurityUtils.getCurrentUsername();
        EventReviewResponseDto responseDto = reviewService.updateReview(eventId, reviewId, reviewUpdateDto, username, imageFiles);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable("eventId") Long eventId,
                                                @PathVariable("reviewId") Long reviewId) {
        String username = SecurityUtils.getCurrentUsername();
        reviewService.deleteReview(eventId, reviewId, username);
        return new ResponseEntity<>("삭제 완료.", HttpStatus.OK);
    }
}
