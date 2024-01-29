package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventUpdateDto;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.FileUtils;
import com.happiday.Happi_Day.utils.HashtagUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final TeamRepository teamRepository;
    private final FileUtils fileUtils;
    private final RedisService redisService;
    private final DefaultImageUtils defaultImageUtils;



    @Transactional
    public EventResponseDto createEvent(
            EventCreateDto request, MultipartFile thumbnailFile, MultipartFile imageFile, String username) {

        String thumbnailUrl;

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));


        if (thumbnailFile == null || thumbnailFile.isEmpty()) {
            thumbnailUrl = defaultImageUtils.getDefaultImageUrlEventThumbnail();
        } else {
            thumbnailUrl = fileUtils.uploadFile(thumbnailFile);
        }

        String imageUrl = fileUtils.uploadFile(imageFile);

        // hashTag 처리
        //get Left, Middle, Right가 각각 1, 2, 3번째 요소 반환
        HashtagUtils hashtagUtils = new HashtagUtils(artistRepository, teamRepository);
        Triple<List<Artist>, List<Team>, List<Hashtag>> processedTags = hashtagUtils.processTags(request.getHashtags());

        Event event = Event.builder()
                .user(user)
                .title(request.getTitle())
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .artists(processedTags.getLeft())
                .teams(processedTags.getMiddle())
                .hashtags(processedTags.getRight())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .address(request.getAddress())
                .location(request.getLocation())
                .build();

        eventRepository.save(event);
        return EventResponseDto.fromEntity(event);
    }


    public Page<EventListResponseDto> readEvents(Pageable pageable, String filter, String keyword) {
        log.info("이벤트 리스트 조회");
        Page<Event> events = eventRepository.findEventsByFilterAndKeyword(pageable, filter, keyword);

        return events.map(EventListResponseDto::fromEntity);
    }

    public Page<EventListResponseDto> readOngoingEvents(Pageable pageable, String filter, String keyword) {
        log.info("진행중인 이벤트 리스트 조회");
        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndOngoing(pageable, filter, keyword);

        return events.map(EventListResponseDto::fromEntity);
    }

    public Page<EventListResponseDto> readEventsBySubscribedArtists(Pageable pageable, String filter, String keyword, String username) {
        log.info("내가 구독한 아티스트의 이벤트 리스트 조회");
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndSubscribedArtists(pageable, filter, keyword, user);

        return events.map(EventListResponseDto::fromEntity);
    }

    public Page<EventListResponseDto> readOngoingEventsBySubscribedArtists(
            Pageable pageable, String filter, String keyword, String username) {

        log.info("내가 구독한 아티스트의 진행중인 이벤트 리스트 조회");

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Event> events = eventRepository.findEventsByFilterAndKeywordAndOngoingAndSubscribedArtists(pageable, filter, keyword, user);

        return events.map(EventListResponseDto::fromEntity);
    }

    @Transactional
    public EventResponseDto readEvent(String clientAddress, Long eventId) {
        log.info("이벤트 단일 조회");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        if (redisService.isFirstIpRequest(clientAddress, eventId)) {
            log.debug("same user requests duplicate in 24hours: {}, {}", clientAddress, eventId);
            increaseViewCount(clientAddress, eventId);
        }

        return EventResponseDto.fromEntity(event);
    }

    public EventResponseDto readMapEvent(Long eventId) {
        log.info("이벤트 지도 조회 테스트");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        return EventResponseDto.fromEntity(event);
    }


        @Transactional
    public EventResponseDto updateEvent(Long eventId, EventUpdateDto request, MultipartFile thumbnailFile, MultipartFile imageFile, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        if (!event.getUser().equals(user)) {
                throw new CustomException(ErrorCode.FORBIDDEN);
        }


        // 이미지 업로드 추가
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            String newThumbnailUrl = fileUtils.uploadFile(thumbnailFile);
            event.setThumbnailUrl(newThumbnailUrl);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = fileUtils.uploadFile(imageFile);
            event.setImageUrl(newImageUrl);
        }

            // hashTag 처리
            //get Left, Middle, Right가 각각 1, 2, 3번째 요소 반환
            HashtagUtils hashtagUtils = new HashtagUtils(artistRepository, teamRepository);
            Triple<List<Artist>, List<Team>, List<Hashtag>> processedTags = hashtagUtils.processTags(request.getHashtags());

            event.update(Event.builder()
                .user(user)
                .title(request.getTitle())
                .artists(processedTags.getLeft())
                .teams(processedTags.getMiddle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .description(request.getDescription())
                .location(request.getLocation())
                .address(request.getAddress())
                .hashtags(processedTags.getRight())
                .build());

        eventRepository.save(event);
        return EventResponseDto.fromEntity(event);
    }



    @Transactional
    public void deleteEvent(Long eventId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.getUsername().equals(username)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));
        eventRepository.delete(event);
    }

    @Transactional
    public String likeEvent(Long eventId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        boolean isLiked = event.getLikes().contains(user);

        String response = "";
        if (isLiked) {
            // 이미 좋아요를 한 경우, 좋아요 취소
            user.getEventLikes().remove(event);
            event.getLikes().remove(user);
            response = "좋아요 취소";
        } else {
            // 좋아요를 하지 않은 경우, 좋아요
            user.getEventLikes().add(event);
            event.getLikes().add(user);
            response = "좋아요 성공";
        }

        eventRepository.save(event);
        return response + " / 좋아요 개수 : " + event.getLikeCount();
    }

    @Transactional
    public String joinEvent(Long eventId, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new CustomException(ErrorCode.EVENT_NOT_FOUND));

        // 이미 참여 중인 이벤트
        boolean isJoined = user.getEventJoinList().stream()
                .anyMatch(joinList -> joinList.equals(event));

        // 진행 중인 이벤트
        boolean isOngoingEvent = event.getStartTime().isBefore(LocalDateTime.now())
                && event.getEndTime().isAfter(LocalDateTime.now());

        String response = "";

        if (isOngoingEvent) {
            if (isJoined) {
                // 이미 참여한 경우, 취소
                user.getEventJoinList().remove(event);
                event.getJoinList().remove(user);
                response = " 이벤트 참여 취소";
            } else {
                // 참여하지 않은 경우, 참여
                user.getEventJoinList().add(event);
                event.getJoinList().add(user);
                response = " 이벤트 참여";
            }
        } else {
            throw new CustomException(ErrorCode.EVENT_NOT_ONGOING);
        }

        eventRepository.save(event);
        return event.getTitle() + response;
    }

    // 조회수
    @Transactional
    public void increaseViewCount(String clientAddress, Long eventId) {
        eventRepository.increaseViewCount(eventId);
        redisService.clientRequest(clientAddress, eventId);
    }
}
