package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesHashtag;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SalesHashtagRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    SalesHashtagRepository salesHashtagRepository;

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
                .endTime(LocalDateTime.of(2024, 8, 1, 10, 00))
                .products(new ArrayList<>())
                .deliveries(new ArrayList<>())
                .build();
        salesRepository.save(sales);
    }

    @Test
    @DisplayName("판매글의 해시태그 삭제")
    public void deleteBySalesTest() {
        // given
        // 해시태그 생성
        Hashtag hashtag1 = Hashtag.builder()
                .tag("hashtag1")
                .build();
        hashtagRepository.save(hashtag1);

        Hashtag hashtag2 = Hashtag.builder()
                .tag("hashtag2")
                .build();
        hashtagRepository.save(hashtag2);

        SalesHashtag salesHashtag1 = SalesHashtag.builder()
                .hashtag(hashtag1)
                .sales(sales)
                .build();
        SalesHashtag salesHashtag2 = SalesHashtag.builder()
                .hashtag(hashtag2)
                .sales(sales)
                .build();
        salesHashtagRepository.save(salesHashtag1);
        salesHashtagRepository.save(salesHashtag2);

        // when
        salesHashtagRepository.deleteBySales(sales);

        // then
        boolean exist1 = salesHashtagRepository.existsById(salesHashtag1.getId());
        boolean exist2 = salesHashtagRepository.existsById(salesHashtag2.getId());
        assertThat(exist1).isFalse();
        assertThat(exist2).isFalse();

    }
}
