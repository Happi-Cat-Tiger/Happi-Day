package com.happiday.Happi_Day.initialization;

import com.happiday.Happi_Day.initialization.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitManager {

    private final UserInitService userInitService;
    private final BoardInitService boardInitService;
    private final ArticleInitService articleInitService;
    private final TeamInitService teamInitService;
    private final ArtistInitService artistInitService;
    private final EventInitService eventInitService;
    private final EventCommentInitService eventCommentInitService;
    private final EventReviewInitService eventReviewInitService;
    private final ArticleCommentInitService articleCommentInitService;
    private final SalesCategoryInitService salesCategoryInitService;
    private final SalesInitService salesInitService;
    private final ProductInitService productInitService;
    private final DeliveryInitService deliveryInitService;
    private final OrderInitService orderInitService;

    public void init() {
        log.info("initialize start~~");
        userInitService.initUsers();
        boardInitService.initBoards();
        teamInitService.initTeams();
        artistInitService.initArtists();
        articleInitService.initArticles();
        articleCommentInitService.initArticleComments();
        eventInitService.initEvents();
        eventCommentInitService.initEventComments();
        eventReviewInitService.initEventReviews();
        salesCategoryInitService.initSalesCategories();
        salesInitService.initSales();
        productInitService.initProducts();
        deliveryInitService.initDeliveries();
        orderInitService.initOrders();

    }
}
