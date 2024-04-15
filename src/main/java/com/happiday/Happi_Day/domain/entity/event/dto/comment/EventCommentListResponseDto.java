package com.happiday.Happi_Day.domain.entity.event.dto.comment;

import com.happiday.Happi_Day.domain.entity.event.EventComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EventCommentListResponseDto {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long eventId;
    private String nickname;
    private String userProfileUrl;

    public static EventCommentListResponseDto fromEntity(EventComment comment) {
        return EventCommentListResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .eventId(comment.getEvent().getId())
                .nickname(comment.getUser().getNickname())
                .userProfileUrl(comment.getUser().getImageUrl())
                .build();
    }
}
