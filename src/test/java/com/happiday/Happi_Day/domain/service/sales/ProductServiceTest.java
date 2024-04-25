package com.happiday.Happi_Day.domain.service.sales;

import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateProductDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ProductRepository;
import com.happiday.Happi_Day.domain.repository.SalesCategoryRepository;
import com.happiday.Happi_Day.domain.repository.SalesRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.ProductService;
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
public class ProductServiceTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    private User testUser;
    private Sales sales;
    private Product product;

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

        // 이미지 생성
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
        product = Product.builder()
                .sales(sales)
                .name("test product1")
                .price(1000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product);
        productList.add(product);

        sales.updateProducts(productList);
    }

    @Test
    @DisplayName("옵션 생성")
    public void createProductTest(){
        // given
        CreateProductDto dto = CreateProductDto.builder()
                .name("test product2")
                .price(1000)
                .stock(100)
                .build();

        // when
        productService.createProduct(sales.getId(), dto, testUser.getUsername());

        // then
        assertThat(productRepository.findByNameAndSales("test product2", sales).get()).isNotNull();
    }

    @Test
    @DisplayName("옵션 수정")
    public void updateProductTest(){
        // given
        CreateProductDto dto = CreateProductDto.builder()
                .name("updated test product")
                .price(2000)
                .stock(200)
                .build();

        // when
        productService.updateProduct(sales.getId(), product.getId(), dto, testUser.getUsername());

        // then
        assertThat(productRepository.findByNameAndSales("updated test product", sales)).isNotNull();
    }
}
