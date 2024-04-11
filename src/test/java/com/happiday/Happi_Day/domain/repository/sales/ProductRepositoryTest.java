package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ProductRepository;
import com.happiday.Happi_Day.domain.repository.SalesCategoryRepository;
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
public class ProductRepositoryTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    ProductRepository productRepository;

    private User testUser;
    private Sales sales;
    private Product product1;
    private Product product2;

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

        // 옵션 생성
        List<Product> productList = new ArrayList<>();
        product1 = Product.builder()
                .sales(sales)
                .name("test product1")
                .price(1000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product1);
        productList.add(product1);

        product2 = Product.builder()
                .sales(sales)
                .name("test product2")
                .price(2000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product2);
        productList.add(product2);
    }

    @Test
    @DisplayName("판매글 옵션 삭제")
    public void deleteAllBySalesTest() {
        // when
        productRepository.deleteAllBySales(sales);

        // then
        boolean exists1 = productRepository.existsById(product1.getId());
        boolean exists2 = productRepository.existsById(product2.getId());
        assertThat(exists1).isFalse();
        assertThat(exists2).isFalse();
    }

    @Test
    @DisplayName("옵션명과 판매글로 옵션 조회")
    public void findByNameAndSalesTest() {
        // given
        String product1Name = product1.getName();
        String product2Name = product2.getName();

        // when
        Optional<Product> foundProduct1 = productRepository.findByNameAndSales(product1Name, sales);
        Optional<Product> foundProduct2 = productRepository.findByNameAndSales(product2Name, sales);

        // then
        assertThat(foundProduct1.get()).isEqualTo(product1);
        assertThat(foundProduct2.get()).isEqualTo(product2);
    }

    @Test
    @DisplayName("옵션명으로 존재하는지 조회")
    public void existsByNameTest() {
        // given
        String productName = product1.getName();

        // when
        boolean exists = productRepository.existsByName(productName);

        // then
        assertThat(exists).isTrue();
    }

}
