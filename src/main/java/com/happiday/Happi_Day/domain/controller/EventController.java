package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.event.dto.EventCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventUpdateDto;
import com.happiday.Happi_Day.domain.service.EventService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
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



@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventResponseDto> createEvent(
            @Valid @RequestPart(value = "event") EventCreateDto eventCreateDto,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestPart(value = "imageFile") MultipartFile imageFile
            ){
        String username = SecurityUtils.getCurrentUsername();
        EventResponseDto responseDto = eventService.createEvent(eventCreateDto, thumbnailFile, imageFile, username);
        log.info("이벤트 게시글 작성");
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponseDto> readEvent(HttpServletRequest request, @PathVariable Long eventId){

        String clientAddress = request.getRemoteAddr();

        EventResponseDto responseDto = eventService.readEvent(clientAddress, eventId);

        log.info("이벤트 단일 조회");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<EventListResponseDto>> readEvents(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword

    ){

        Page<EventListResponseDto> responseDtoList = eventService.readEvents(pageable, filter, keyword);
        log.info("이벤트 리스트 조회");
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<Page<EventListResponseDto>> readOngoingEvents(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword

    ){

        Page<EventListResponseDto> responseDtoList = eventService.readOngoingEvents(pageable, filter, keyword);
        log.info("진행 중인 이벤트 리스트 조회");
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/subscribedArtists")
    public ResponseEntity<Page<EventListResponseDto>> readEventsBySubscribedArtists(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword

    ){
        String username = SecurityUtils.getCurrentUsername();

        Page<EventListResponseDto> responseDtoList = eventService.readEventsBySubscribedArtists(pageable, filter, keyword, username);
        log.info("내가 구독한 아티스트/팀의 이벤트 리스트 조회");
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @GetMapping("/subscribedArtists/ongoing")
    public ResponseEntity<Page<EventListResponseDto>> readOngoingEventsBySubscribedArtists(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword

    ){
        String username = SecurityUtils.getCurrentUsername();

        Page<EventListResponseDto> responseDtoList = eventService.readOngoingEventsBySubscribedArtists(pageable, filter, keyword, username);
        log.info("내가 구독한 아티스트/팀의 진행중인 이벤트 리스트 조회");
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    @PutMapping(value = "{eventId}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<EventResponseDto> updateEvent(
            @PathVariable Long eventId,
            @RequestPart(value = "event") EventUpdateDto eventUpdateDto,
            @RequestParam(value = "thumbnailFile", required = false) MultipartFile thumbnailFile,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ){
        String username = SecurityUtils.getCurrentUsername();
        EventResponseDto responseDto = eventService.updateEvent(eventId, eventUpdateDto, thumbnailFile, imageFile, username);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long eventId){
        String username = SecurityUtils.getCurrentUsername();
        eventService.deleteEvent(eventId, username);
        return new ResponseEntity<>("삭제 완료.", HttpStatus.OK);
    }

    @PostMapping("/{eventId}/like")
    public ResponseEntity<String> likeEvent(@PathVariable Long eventId){
        String username = SecurityUtils.getCurrentUsername();
        String response = eventService.likeEvent(eventId, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{eventId}/join")
    public ResponseEntity<String> joinEvent(@PathVariable Long eventId){
        String username = SecurityUtils.getCurrentUsername();
        String response = eventService.joinEvent(eventId, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
