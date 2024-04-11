package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QuerySalesRepositoryTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    QuerySalesRepository querySalesRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistSalesRepository artistSalesRepository;

    @Autowired
    ArtistSubscriptionRepository artistSubscriptionRepository;

    private User testUser;
    private Sales sales;

    @BeforeEach
    void beforeEach() {
        // 카테고리 생성
        SalesCategory testCategory = SalesCategory.builder()
                .name("test Category")
                .build();
        salesCategoryRepository.save(testCategory);

        // 유저 생성
        testUser = User.builder()
                .username("test email")
                .password("test password")
                .nickname("test nickname")
                .realname("test name")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        testUser = userRepository.save(testUser);

        // 이미지 url 리스트
        List<String> imageList = new ArrayList<>();

        imageList.add("http://example.com/sales_image1.jpg");
        imageList.add("http://example.com/sales_image2.jpg");
        imageList.add("http://example.com/sales_image3.jpg");

        // 판매글 생성
        sales = Sales.builder()
                .salesCategory(testCategory)
                .users(testUser)
                .name("test name")
                .namePrice(10000)
                .description("test description")
                .salesStatus(SalesStatus.ON_SALE)
                .thumbnailImage("http://example.com/sales_thumnail.jpg")
                .imageUrl(imageList)
                .accountName("test accountName")
                .accountUser("test accountUser")
                .accountNumber("test accountNumber")
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 4, 3, 10, 00))
                .products(new ArrayList<>())
                .deliveries(new ArrayList<>())
                .build();
        salesRepository.save(sales);

        // 아티스트 생성
        Artist artist = artistRepository.save(Artist.builder()
                .name("test artist")
                .description("test artist description")
                .profileUrl("http://example.com/profile.jpg")
                .build());

        // 아티스트-판매글 연관관계 설정
        ArtistSales artistSales = artistSalesRepository.save(ArtistSales.builder()
                .sales(sales)
                .artist(artist)
                .build());

        // 아티스트-유저 연관관계 설정
        ArtistSubscription artistSubscription = artistSubscriptionRepository.save(ArtistSubscription.builder()
                .user(testUser)
                .artist(artist)
                .subscribedAt(LocalDateTime.now())
                .build());
        List<ArtistSubscription> artistSubscriptionList = new ArrayList<>();
        artistSubscriptionList.add(artistSubscription);

        User updateUser = testUser.toBuilder()
                .artistSubscriptionList(artistSubscriptionList)
                .teamSubscriptionList(new ArrayList<>())
                .build();
        userRepository.save(updateUser);
    }

    @Test
    @DisplayName("필터링 판매글 조회")
    public void findSalesByFilterAndKeywordTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test name";
        Long categoryId = sales.getSalesCategory().getId();

        // when
        Page<Sales> foundSales = querySalesRepository.findSalesByFilterAndKeyword(pageable, categoryId, filter, keyword);

        // then
        assertThat(foundSales).isNotNull();
        assertThat(foundSales.getContent().get(0).getName()).isEqualTo("test name");
    }

    @Test
    @DisplayName("진행중인 판매글 조회")
    public void findSalesByFilterAndKeywordOngoingTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test name";
        Long categoryId = sales.getSalesCategory().getId();

        // when
        Page<Sales> foundSales = querySalesRepository.findSalesByFilterAndKeywordOngoing(pageable, categoryId, filter, keyword);

        // then
        assertThat(foundSales.getContent()).isEmpty();
    }

    @Test
    @DisplayName("구독한 아티스트의 글 중 필터링 판매글 조회")
    public void findSalesByFilterAndKeywordAndSubscribedArtistsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test name";
        Long categoryId = sales.getSalesCategory().getId();

        // when
        Page<Sales> foundSales = querySalesRepository.findSalesByFilterAndKeywordAndSubscribedArtists(pageable, categoryId, filter, keyword, testUser);

        // then
        assertThat(foundSales).isNotNull();
        assertThat(foundSales.getContent().get(0).getName()).isEqualTo("test name");
    }

    @Test
    @DisplayName("구독한 아티스트 글 중 판매중인 필터링 판매글 조회")
    public void findSalesByFilterAndKeywordAndOngoingAndSubscribedArtistsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test name";
        Long categoryId = sales.getSalesCategory().getId();

        // when
        Page<Sales> foundSales = querySalesRepository.findSalesByFilterAndKeywordAndOngoingAndSubscribedArtists(pageable, categoryId, filter, keyword, testUser);

        // then
        assertThat(foundSales.getContent()).isEmpty();
    }

}
