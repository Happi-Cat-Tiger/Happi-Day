package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesLike;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.SalesCategoryRepository;
import com.happiday.Happi_Day.domain.repository.SalesLikeRepository;
import com.happiday.Happi_Day.domain.repository.SalesRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SalesLikeRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesLikeRepository salesLikeRepository;

    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    SalesRepository salesRepository;

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

        imageList.add("http://example.com/article_image1.jpg");
        imageList.add("http://example.com/article_image2.jpg");
        imageList.add("http://example.com/article_image3.jpg");

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
                .endTime(LocalDateTime.of(2024, 8, 1, 10, 00))
                .products(new ArrayList<>())
                .deliveries(new ArrayList<>())
                .build();
        salesRepository.save(sales);

        // 좋아요 유저 리스트 생성
        SalesLike salesLike = SalesLike.builder()
                .sales(sales)
                .user(testUser)
                .build();
        salesLikeRepository.save(salesLike);
    }

    @Test
    @DisplayName("유저가 판매글에 찜하기를 했는지 확인")
    public void findByUserAndSalesTest() {
        // when
        Optional<SalesLike> foundLike = salesLikeRepository.findByUserAndSales(testUser, sales);

        // then
        assertThat(foundLike).isPresent();
        assertThat(foundLike.get().getUser()).isEqualTo(testUser);
        assertThat(foundLike.get().getSales()).isEqualTo(sales);
    }

    @Test
    @DisplayName("판매글에 찜하기를 누른 유저 조회")
    public void findBySalesTest() {
        // when
        List<SalesLike> likes = salesLikeRepository.findBySales(sales);

        // then
        assertThat(likes.size()).isEqualTo(1);
        assertThat(likes.get(0).getSales()).isEqualTo(sales);
    }
}
