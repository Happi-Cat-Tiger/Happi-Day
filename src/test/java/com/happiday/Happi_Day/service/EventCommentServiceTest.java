package com.happiday.Happi_Day.service;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventComment;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentUpdateDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventCommentRepository;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.EventCommentService;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@Transactional
public class EventCommentServiceTest {

    @InjectMocks
    EventCommentService commentService;

    @Mock
    EventCommentRepository commentRepository;

    @Mock
    EventRepository eventRepository;

    @Mock
    UserRepository userRepository;

    private User user1;

    private Event event1;

    private EventComment comment1;

    private EventComment comment2;


    @BeforeEach
    void setup() {
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

        Artist artist1 = Artist.builder()
                .name("아이유")
                .description("아이유입니다.")
                .build();

        Artist artist2 = Artist.builder()
                .name("김범수")
                .description("김범수입니다.")
                .build();


        Team team1 = Team.builder()
                .name("동방신기")
                .description("동방신기입니다.")
                .build();

        Team team2 = Team.builder()
                .name("소녀시대")
                .description("소녀시대입니다.")
                .build();

        Hashtag hashtag1 = Hashtag.builder()
                .tag("동방신기포에버")
                .build();

        Hashtag hashtag2 = Hashtag.builder()
                .tag("소녀시대포에버")
                .build();

        MockMultipartFile eventImage1 = new MockMultipartFile(
                "multipartFile1", "eventImage1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        event1 = Event.builder()
                .title("제목")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl(String.valueOf(eventImage1))
                .eventHashtags(new ArrayList<>())
                .build();

        comment1 = EventComment.builder()
                .user(user1)
                .event(event1)
                .content("eventComment test content 1")
                .build();
        comment2 = EventComment.builder()
                .user(user1)
                .event(event1)
                .content("eventComment test content 2")
                .build();
    }

    @Test
    @DisplayName("댓글 작성 테스트")
    void createCommentTest() {
        EventCommentCreateDto request = EventCommentCreateDto.builder()
                .content(comment1.getContent())
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));

        EventCommentResponseDto result = commentService.createComment(event1.getId(), request, user1.getNickname());

        assertNotNull(result);
        assertEquals(comment1.getContent(), result.getContent());
        verify(commentRepository).save(any(EventComment.class));

    }

    @Test
    @DisplayName("댓글 조회 테스트")
    void readCommentsTest() {
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(commentRepository.findAllByEvent(any(Event.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(comment1, comment2)));

        Pageable pageable = PageRequest.of(0, 10);
        Page<EventCommentResponseDto> comments = commentService.readComments(event1.getId(), pageable);

        assertNotNull(comments);
        assertEquals(2, comments.getTotalElements());
        assertEquals(comment1.getContent(), comments.getContent().get(0).getContent());
        assertEquals(comment2.getContent(), comments.getContent().get(1).getContent());
        verify(commentRepository).findAllByEvent(any(Event.class), any(Pageable.class));
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateCommentTest() {
        EventCommentUpdateDto request = EventCommentUpdateDto.builder()
                .content("updated content")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

        EventCommentResponseDto result = commentService.updateComment(event1.getId(), comment1.getId(), request, user1.getUsername());

        assertNotNull(result);
        assertEquals("updated content", result.getContent());
        verify(commentRepository).save(any(EventComment.class));
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void deleteCommentTest() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

        commentService.deleteComment(event1.getId(), comment1.getId(), user1.getUsername());

        verify(commentRepository).delete(any(EventComment.class));
    }

    @Test
    @DisplayName("댓글 작성 시 이벤트가 삭제된 경우 테스트")
    void createCommentWithDeletedEventTest() {
        Event deletedEvent = Event.builder()
                .title("제목")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl("eventImage1.jpg")
                .eventHashtags(new ArrayList<>())
                .deletedAt(LocalDateTime.now())
                .build();

        EventCommentCreateDto request = EventCommentCreateDto.builder()
                .content("new comment")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(deletedEvent.getId())).thenReturn(Optional.of(deletedEvent));

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.createComment(deletedEvent.getId(), request, user1.getNickname());
        });

        assertEquals(ErrorCode.EVENT_ALREADY_DELETED, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 작성 시 유저가 존재하지 않는 경우 테스트")
    void createCommentWithNoExistUserTest() {
        EventCommentCreateDto request = EventCommentCreateDto.builder()
                .content("new comment")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.createComment(event1.getId(), request, "noExistUser");
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 시 이벤트가 삭제된 경우 테스트")
    void updateCommentWithDeletedEventTest() {
        EventCommentUpdateDto request = EventCommentUpdateDto.builder()
                .content("updated content")
                .build();

        Event deletedEvent = Event.builder()
                .title("제목")
                .user(user1)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl("eventImage1.jpg")
                .eventHashtags(new ArrayList<>())
                .deletedAt(LocalDateTime.now())
                .build();

        // 스터빙 허용
        lenient().when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        lenient().when(eventRepository.findById(deletedEvent.getId())).thenReturn(Optional.of(deletedEvent));
        lenient().when(commentRepository.findById(comment1.getId())).thenReturn(Optional.of(comment1));

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.updateComment(deletedEvent.getId(), comment1.getId(), request, user1.getUsername());
        });

        assertEquals(ErrorCode.EVENT_ALREADY_DELETED, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 시 댓글이 존재하지 않는 경우 테스트")
    void updateCommentWithNoExistCommentTest() {
        EventCommentUpdateDto request = EventCommentUpdateDto.builder()
                .content("updated content")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.updateComment(event1.getId(), 999L, request, user1.getUsername());
        });

        assertEquals(ErrorCode.EVENT_COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 시 댓글이 이미 삭제된 경우 테스트")
    void deleteCommentWithAlreadyDeletedCommentTest() {
        EventComment deletedComment = EventComment.builder()
                .user(user1)
                .event(event1)
                .content("deleted comment")
                .deletedAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user1));
        when(eventRepository.findById(event1.getId())).thenReturn(Optional.of(event1));
        when(commentRepository.findById(deletedComment.getId())).thenReturn(Optional.of(deletedComment));

        CustomException exception = assertThrows(CustomException.class, () -> {
            commentService.deleteComment(event1.getId(), deletedComment.getId(), user1.getUsername());
        });

        assertEquals(ErrorCode.EVENT_COMMENT_ALREADY_DELETED, exception.getErrorCode());
    }
}
