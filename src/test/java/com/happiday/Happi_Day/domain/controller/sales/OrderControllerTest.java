package com.happiday.Happi_Day.domain.controller.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.product.Order;
import com.happiday.Happi_Day.domain.entity.product.dto.OrderRequestDto;
import com.happiday.Happi_Day.domain.entity.product.dto.UpdateOrderDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.domain.service.OrderService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class OrderControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    OrderService orderService;

    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistSubscriptionRepository artistSubscriptionRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    SalesHashtagRepository salesHashtagRepository;

    @Autowired
    ArtistSalesRepository artistSalesRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    OrderedProductRepository orderedProductRepository;

    @Autowired
    ObjectMapper objectMapper;

    private User testUser;
    private SalesCategory testCategory;
    private Sales sales;
    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private Order order;

    @BeforeAll
    public static void beforeAll() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        securityUtilsMockedStatic.close();
    }

    @BeforeEach
    void beforeEach() {
        // 카테고리 생성
        testCategory = SalesCategory.builder()
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
                .artistSubscriptionList(new ArrayList<>())
                .teamSubscriptionList(new ArrayList<>())
                .build();
        userRepository.save(testUser);

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
                .salesHashtags(new ArrayList<>())
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
    public void orderTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

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
        String body = objectMapper.writeValueAsString(orderDto);
        MockMultipartFile orderPart = new MockMultipartFile("order", "", MediaType.APPLICATION_JSON_VALUE, body.getBytes());

        mockMvc.perform(
                        multipart("/api/v1/sales/" + sales.getId() + "/order")
                                .file(orderPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 상세 조회")
    public void readOneOrderTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/sales/" + sales.getId() + "/order/" + order.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("ORDER_COMPLETED"));
    }

    @Test
    @DisplayName("판매글 주문 목록 조회")
    public void readOrderListForSalesTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(get("/api/v1/sales/" + sales.getId() + "/order"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("주문 취소 상태로 변경")
    public void orderCancelTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(put("/api/v1/sales/" + sales.getId() + "/order/" + order.getId() + "/cancel"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 삭제")
    public void orderDeleteTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        orderService.orderCancel(sales.getId(), order.getId(), testUser.getUsername());

        mockMvc.perform(delete("/api/v1/sales/" + sales.getId() + "/order/" + order.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("주문 상태 변경")
    public void changeStatusTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UpdateOrderDto dto = UpdateOrderDto.builder()
                .orderStatus("배송중")
                .trackingNum("12345678")
                .build();
        String orderBody = objectMapper.writeValueAsString(dto);
        MockMultipartFile orderPart = new MockMultipartFile("status", "", MediaType.APPLICATION_JSON_VALUE, orderBody.getBytes());

        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId() + "/order/" + order.getId() + "/changeStatus")
                                .file(orderPart)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                ).andDo(print())
                .andExpect(status().isOk());
    }
}

