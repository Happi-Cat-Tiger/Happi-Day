package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventComment;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentUpdateDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventCommentRepository;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventCommentService {
    private final EventCommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventCommentResponseDto createComment(Long eventId, EventCommentCreateDto request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        if(event.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.EVENT_ALREADY_DELETED);
        }

        EventComment comment = EventComment.builder()
                .user(user)
                .content(request.getContent())
                .event(event)
                .build();

        commentRepository.save(comment);

        return EventCommentResponseDto.fromEntity(comment);
    }

    public Page<EventCommentResponseDto> readComments(Long eventId, Pageable pageable) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        Page<EventComment> commentList = commentRepository.findAllByEvent(event, pageable);

        return commentList.map(EventCommentResponseDto::fromEntity);
    }

    @Transactional
    public EventCommentResponseDto updateComment(Long eventId, Long commentId, EventCommentUpdateDto request, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        if(event.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.EVENT_ALREADY_DELETED);
        }

        EventComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_COMMENT_NOT_FOUND));

        if(comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.EVENT_COMMENT_ALREADY_DELETED);
        }

        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        comment.update(request.toEntity());
        commentRepository.save(comment);

        return EventCommentResponseDto.fromEntity(comment);
    }

    @Transactional
    public void deleteComment(Long eventId, Long commentId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        EventComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_COMMENT_NOT_FOUND));

        if(comment.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.EVENT_COMMENT_ALREADY_DELETED);
        }

        if (!comment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        commentRepository.delete(comment);

    }
}
