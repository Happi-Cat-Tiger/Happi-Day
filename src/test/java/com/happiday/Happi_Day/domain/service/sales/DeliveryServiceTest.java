package com.happiday.Happi_Day.domain.service.sales;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateDeliveryDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.DeliveryRepository;
import com.happiday.Happi_Day.domain.repository.SalesCategoryRepository;
import com.happiday.Happi_Day.domain.repository.SalesRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.DeliveryService;
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
public class DeliveryServiceTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    DeliveryRepository deliveryRepository;


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
    }

    @Test
    @DisplayName("배송방법 생성")
    public void createDeliveryTest() {
        // given
        CreateDeliveryDto dto = CreateDeliveryDto.builder()
                .name("test delivery name")
                .price(1800)
                .build();

        // when
        deliveryService.createDelivery(sales.getId(), dto, testUser.getUsername());

        // then
        assertThat(deliveryRepository.findAllBySales(sales).size()).isEqualTo(1);
        assertThat(deliveryRepository.findAllBySales(sales).get(0).getName()).isEqualTo("test delivery name");
    }
}
