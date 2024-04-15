package com.happiday.Happi_Day.domain.entity.event.dto.review;


import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.event.ReviewImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class EventReviewResponseDto {

    private Long id;

    private String nickname;

    private String description;

    private int rating;

    private LocalDateTime updatedAt;

    private List<String> imageUrlList;

    private String userProfileUrl;

    public static EventReviewResponseDto fromEntity(EventReview review) {

        return EventReviewResponseDto.builder()
                .id(review.getId())
                .nickname(review.getUser().getNickname())
                .description(review.getDescription())
                .rating(review.getRating())
                .updatedAt(review.getUpdatedAt())
                .imageUrlList(review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()))
                .userProfileUrl(review.getUser().getImageUrl())
                .build();
    }
}
