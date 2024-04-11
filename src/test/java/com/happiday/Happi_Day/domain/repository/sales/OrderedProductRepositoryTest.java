package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.product.*;
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
public class OrderedProductRepositoryTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderedProductRepository orderedProductRepository;

    private User testUser;
    private Sales sales;
    private Order order;

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

        // 판매글 생성
        sales = Sales.builder()
                .salesCategory(testCategory)
                .users(testUser)
                .name("test name")
                .namePrice(10000)
                .description("test description")
                .salesStatus(SalesStatus.ON_SALE)
                .thumbnailImage("http://example.com/sales_thumnail.jpg")
                .imageUrl(new ArrayList<>())
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
        Product product1 = Product.builder()
                .sales(sales)
                .name("test product1")
                .price(1000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product1);
        productList.add(product1);

        Product product2 = Product.builder()
                .sales(sales)
                .name("test product2")
                .price(2000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product2);
        productList.add(product2);

        // 배송방법 생성
        List<Delivery> deliveryList = new ArrayList<>();
        Delivery delivery = Delivery.builder()
                .name("test delivery")
                .price(1800)
                .sales(sales)
                .build();
        deliveryRepository.save(delivery);
        deliveryList.add(delivery);

        // 주문 생성
        order = Order.builder()
                .user(testUser)
                .sales(sales)
                .address("test address")
                .orderStatus(OrderStatus.ORDER_COMPLETED)
                .orderedAt(LocalDateTime.now())
                .depositor("test depositor")
                .refundAccountName("test refundAccountName")
                .refundAccountUser("test refundAccountUser")
                .refundAccountNumber("test refundAccountNumber")
                .orderedProducts(new ArrayList<>())
                .delivery(delivery)
                .build();
        orderRepository.save(order);

        OrderedProduct orderedProduct1 = OrderedProduct.builder()
                .order(order)
                .product(product1)
                .quantity(1)
                .build();
        orderedProductRepository.save(orderedProduct1);
        order.updateOrderedProduct(orderedProduct1);

        OrderedProduct orderedProduct2 = OrderedProduct.builder()
                .order(order)
                .product(product2)
                .quantity(2)
                .build();
        orderedProductRepository.save(orderedProduct2);
        order.updateOrderedProduct(orderedProduct2);
    }

    @Test
    @DisplayName("주문된 옵션 조회")
    public void findAllByOrderTest() {
        // when
        List<OrderedProduct> foundProducts = orderedProductRepository.findAllByOrder(order);

        // then
        assertThat(foundProducts.size()).isEqualTo(2);
        assertThat(foundProducts.get(0).getProduct().getName()).isEqualTo("test product1");
    }
}
