package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleComment;
import com.happiday.Happi_Day.domain.entity.article.ArticleLike;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadListArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadListCommentDto;
import com.happiday.Happi_Day.domain.entity.event.*;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.product.Order;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesLike;
import com.happiday.Happi_Day.domain.entity.product.dto.*;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    private final SalesRepository salesRepository;
    private final SalesLikeRepository salesLikeRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final EventReviewRepository reviewRepository;
    private final EventParticipationRepository eventParticipationRepository;
    private final EventLikeRepository eventLikeRepository;

    public Page<ReadListArticleDto> readMyArticles(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<Article> articles = articleRepository.findAllByUser(user, pageable);

        return articles.map(ReadListArticleDto::fromEntity);
    }

    public Page<ReadListCommentDto> readMyArticleComments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<ArticleComment> articleComments = articleCommentRepository.findAllByUser(user, pageable);

        return articleComments.map(ReadListCommentDto::fromEntity);
    }

    public Page<ReadListArticleDto> readLikeArticles(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<ArticleLike> articleLikes = articleLikeRepository.findByUser(user, pageable);

        return articleLikes.map(articleLike -> ReadListArticleDto.fromEntity(articleLike.getArticle()));
    }

    public Page<EventListResponseDto> readMyEvents(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<Event> events = eventRepository.findAllByUser(user, pageable);

        return events.map(EventListResponseDto::fromEntity);
    }

    public Page<EventCommentListResponseDto> readMyEventComments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<EventComment> eventComments = eventCommentRepository.findAllByUser(user, pageable);

        return eventComments.map(EventCommentListResponseDto::fromEntity);
    }

    public Page<EventListResponseDto> readLikeEvents(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<EventLike> eventLikes = eventLikeRepository.findByUser(user, pageable);

        return eventLikes.map(eventLike -> EventListResponseDto.fromEntity(eventLike.getEvent()));
    }

    public Page<EventListResponseDto> readJoinEvents(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<EventParticipation> eventParticipationList = eventParticipationRepository.findByUser(user, pageable);

        return eventParticipationList.map(eventParticipation -> EventListResponseDto.fromEntity(eventParticipation.getEvent()));
    }

    public Page<EventReviewResponseDto> getMyReviews(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<EventReview> reviews = reviewRepository.findAllByUser(user, pageable);

        return reviews.map(EventReviewResponseDto::fromEntity);
    }

    public Page<ReadListSalesDto> readMySales(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<Sales> sales = salesRepository.findAllByUsers(user, pageable);

        return sales.map(ReadListSalesDto::fromEntity);
    }

    public Page<ReadListSalesDto> readLikeSales(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<SalesLike> salesLikes = salesLikeRepository.findByUser(user, pageable);

        return salesLikes.map(salesLike -> ReadListSalesDto.fromEntity(salesLike.getSales()));
    }

    public Page<ReadListOrderDto> readMyOrders(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<Order> orders = orderRepository.findAllByUser(user, pageable);

        return orders.map(ReadListOrderDto::fromEntity);
    }

}
