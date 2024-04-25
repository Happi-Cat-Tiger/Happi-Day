package com.happiday.Happi_Day.domain.service.sales;

import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.product.dto.*;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.domain.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class OrderServiceTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    DeliveryRepository deliveryRepository;

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

        // 썸네일 이미지 생성
        MultipartFile thumbnailImage = new MockMultipartFile("thumbnailImage", "thumbnailImage.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        // 이미지 생성
        List<MultipartFile> imageList = new ArrayList<>();
        MultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        MultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        MultipartFile image3 = new MockMultipartFile("image3", "image3.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
        imageList.add(image1);
        imageList.add(image2);
        imageList.add(image3);

        // 판매글 생성
        sales = Sales.builder()
                .salesCategory(testCategory)
                .users(testUser)
                .name("test name1")
                .namePrice(10000)
                .description("test description1")
                .salesStatus(SalesStatus.ON_SALE)
                .thumbnailImage(thumbnailImage.toString())
                .imageUrl(imageList.stream().map(image -> image.toString()).collect(Collectors.toList()))
                .accountName("test accountName1")
                .accountUser("test accountUser1")
                .accountNumber("test accountNumber1")
                .salesLikes(new ArrayList<>())
                .orders(new ArrayList<>())
                .artistSalesList(new ArrayList<>())
                .teamSalesList(new ArrayList<>())
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 8, 25, 10, 00))
                .products(new ArrayList<>())
                .deliveries(new ArrayList<>())
                .build();
        salesRepository.save(sales);

        // 옵션 생성
        List<Product> productList = new ArrayList<>();
        Product product = Product.builder()
                .sales(sales)
                .name("test product1")
                .price(1000)
                .stock(100)
                .productStatus(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product);
        productList.add(product);

        sales.updateProducts(productList);

        // 배송방법 생성
        List<Delivery> deliveryList = new ArrayList<>();
        Delivery delivery = Delivery.builder()
                .name("test delivery")
                .price(1800)
                .build();
        deliveryList.add(delivery);
        deliveryRepository.save(delivery);

        sales.updateDelivery(deliveryList);

        // 주문 생성
        order = Order.builder()
                .user(testUser)
                .sales(sales)
                .address("test address1")
                .orderStatus(OrderStatus.ORDER_COMPLETED)
                .orderedAt(LocalDateTime.now())
                .depositor("test depositor1")
                .refundAccountName("test refundAccountName1")
                .refundAccountUser("test refundAccountUser1")
                .refundAccountNumber("test refundAccountNumber1")
                .orderedProducts(new ArrayList<>())
                .delivery(delivery)
                .totalPrice(0)
                .build();
        orderRepository.save(order);

        OrderedProduct orderedProduct = OrderedProduct.builder()
                .order(order)
                .product(product)
                .quantity(1)
                .build();
        orderedProductRepository.save(orderedProduct);
        order.updateOrderedProduct(orderedProduct);

    }

    @Test
    @DisplayName("주문하기")
    public void orderTest() {
        // given
        Map<String, Integer> products = new HashMap<>();
        products.put(sales.getProducts().get(0).getName(), sales.getProducts().get(0).getPrice());
        OrderRequestDto orderDto = OrderRequestDto.builder()
                .products(products)
                .address("test address2")
                .delivery("test delivery")
                .depositor("test depositor2")
                .refundAccountName("test refundAccountName2")
                .refundAccountNumber("test refundAccountNumber2")
                .refundAccountUser("test refundAccountUser2")
                .build();

        // when
        orderService.order(sales.getId(), testUser.getUsername(), orderDto);

        // then
        List<Order> order = orderRepository.findAll();
        assertThat(order.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("주문 단일 상세 조회")
    public void readOneOrderTest() {
        // when
        ReadOneOrderDto readOneOrder = orderService.readOneOrder(sales.getId(), order.getId(), testUser.getUsername());

        // then
        assertThat(readOneOrder.getRefundAccountName()).isEqualTo("test refundAccountName1");
    }

    @Test
    @DisplayName("판매글 주문 목록 조회")
    public void orderListForSalesTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);

        // when
        Page<ReadOrderListForSalesDto> readOrderListForSales = orderService.orderListForSales(sales.getId(), testUser.getUsername(), pageable);

        // then
        assertThat(readOrderListForSales.getContent().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("주문 취소")
    public void orderCancelTest() {
        // when
        orderService.orderCancel(sales.getId(), order.getId(), testUser.getUsername());

        // then
        List<Order> orders = orderRepository.findAll();
        assertThat(orders.get(0).getOrderStatus()).isEqualTo(OrderStatus.ORDER_CANCEL);
    }

    @Test
    @DisplayName("주문 삭제")
    public void orderDeleteTest() {
        // given
        order.updateStatus(OrderStatus.ORDER_CANCEL);

        // when
        orderService.orderDelete(sales.getId(), order.getId(), testUser.getUsername());

        // then
        List<Order> orders = orderRepository.findAll();
        assertThat(orders.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("주문 상태 변경")
    public void changeOrderStatus() {
        // given
        UpdateOrderDto dto = UpdateOrderDto.builder()
                .orderStatus("배송중")
                .trackingNum("test trackingNum").build();

        // when
        orderService.changeOrderStatus(sales.getId(), order.getId(), testUser.getUsername(), dto);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.DELIVERING);
    }
}
