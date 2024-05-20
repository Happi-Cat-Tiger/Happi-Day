package com.happiday.Happi_Day.domain.service.user;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewCreateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.EventRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.EventCommentService;
import com.happiday.Happi_Day.domain.service.EventReviewService;
import com.happiday.Happi_Day.domain.service.EventService;
import com.happiday.Happi_Day.domain.service.MyPageService;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;


@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MyPageServiceTest {

    // Article 관련 MyPageService는 ArticleServiceTest, ArticleCommentService에 있음

    @Autowired
    MyPageService myPageService;

    @Autowired
    EventService eventService;

    @Autowired
    EventCommentService eventCommentService;

    @Autowired
    EventReviewService eventReviewService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    private DefaultImageUtils defaultImageUtils;

    private User testUser;

    private Event testEvent;

    @Value("${mail.address}")
    private String testEmail;

    @BeforeEach
    public void init() {
        testUser = User.builder()
                .username(testEmail)
                .password("qwer1234")
                .nickname("닉네임")
                .realname("테스트")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(testUser);

        testEvent = Event.builder()
                .title("제목")
                .user(testUser)
                .startTime(LocalDateTime.now().minusMonths(1))
                .endTime(LocalDateTime.now().plusMonths(3))
                .description("내용")
                .address("서울특별시 서초구 반포대로30길 32")
                .location("1층 카페 이로")
                .imageUrl(defaultImageUtils.getDefaultImageUrlEventThumbnail())
                .eventHashtags(new ArrayList<>())
                .comments(new ArrayList<>())
                .reviews(new ArrayList<>())
                .likes(new ArrayList<>())
                .eventParticipationList(new ArrayList<>())
                .artistsEventList(new ArrayList<>())
                .teamsEventList(new ArrayList<>())
                .build();
        eventRepository.save(testEvent);
    }
    @Test
    void 내가쓴이벤트조회() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<EventListResponseDto> result = myPageService.readMyEvents(testUser.getUsername(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0).getNickname()).isEqualTo(testUser.getNickname());
    }

    @Test
    void 내가쓴이벤트댓글조회() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .role(RoleType.USER)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        EventCommentCreateDto dto = new EventCommentCreateDto("댓글1등");
        eventCommentService.createComment(testEvent.getId(), dto, user.getUsername());

        // when
        Page<EventCommentListResponseDto> result = myPageService.readMyEventComments(user.getUsername(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0).getNickname()).isEqualTo(user.getNickname());
    }

    @Test
    void 내가좋아요한이벤트조회() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .role(RoleType.USER)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        eventService.likeEvent(testEvent.getId(), user.getUsername());

        // when
        Page<EventListResponseDto> result = myPageService.readLikeEvents(user.getUsername(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0).getNickname()).isEqualTo(testUser.getNickname());
    }

    @Test
    void 내가참여한이벤트조회() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .role(RoleType.USER)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        eventService.joinEvent(testEvent.getId(), user.getUsername());

        // when
        Page<EventListResponseDto> result = myPageService.readJoinEvents(user.getUsername(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0).getNickname()).isEqualTo(testUser.getNickname());
    }

    @Test
    void 내가작성한이벤트리뷰조회() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        User user = User.builder()
                .username("user@email.com")
                .password("password")
                .nickname("테스트")
                .realname("김철수")
                .phone("01012341234")
                .role(RoleType.USER)
                .isTermsAgreed(true)
                .termsAt(LocalDateTime.now())
                .eventReviews(new ArrayList<>())
                .build();
        userRepository.save(user);

        EventReviewCreateDto dto = new EventReviewCreateDto("너무좋아", 5);
        eventReviewService.createReview(testEvent.getId(), dto, new ArrayList<>(), user.getUsername());

        // when
        Page<EventReviewResponseDto> result = myPageService.getMyReviews(user.getUsername(), pageable);

        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent().get(0).getNickname()).isEqualTo(user.getNickname());
    }
}