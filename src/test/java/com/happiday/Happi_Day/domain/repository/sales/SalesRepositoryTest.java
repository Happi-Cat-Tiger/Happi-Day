package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import jakarta.persistence.EntityManager;
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
public class SalesRepositoryTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    SalesLikeRepository salesLikeRepository;

    @Autowired
    EntityManager entityManager;

    private Sales sales;
    private User testUser;

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
    @DisplayName("유저로 판매글 조회")
    public void findAllByUsersTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);

        // when
        Page<Sales> foundSales = salesRepository.findAllByUsers(testUser, pageable);

        // then
        assertThat(foundSales.getContent().get(0).getName()).isEqualTo("test name");
    }

    @Test
    @DisplayName("판매글 제목으로 존재 여부 확인")
    public void existsByNameTest() {
        // when
        boolean exists = salesRepository.existsByName("test name");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("판매글 조회수 증가 확인")
    public void increaseViewCountTest() {
        // given
        int beforeIncrease = sales.getViewCount();

        // when
        salesRepository.increaseViewCount(sales.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        Sales updateSales = salesRepository.findById(sales.getId()).get();
        assertThat(updateSales.getViewCount()).isEqualTo(beforeIncrease + 1);
    }
}
