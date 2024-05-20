package com.happiday.Happi_Day.domain.controller.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesHashtag;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateDeliveryDto;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateProductDto;
import com.happiday.Happi_Day.domain.entity.product.dto.UpdateSalesDto;
import com.happiday.Happi_Day.domain.entity.product.dto.WriteSalesDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
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
import java.util.List;
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
public class SalesControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

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
    ObjectMapper objectMapper;

    private User testUser;
    private SalesCategory testCategory;
    private Artist artist;
    private Sales sales;
    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

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

        // 아티스트 생성
        List<Artist> artists = new ArrayList<>();
        artist = Artist.builder()
                .name("artist")
                .description("artist description")
                .profileUrl("imageUrl")
                .build();
        artistRepository.save(artist);
        artists.add(artist);

        // 구독한 아티스트
        List<ArtistSubscription> subList = new ArrayList<>();
        ArtistSubscription sub = ArtistSubscription.builder()
                .user(testUser)
                .artist(artist)
                .build();
        artistSubscriptionRepository.save(sub);
        subList.add(sub);

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
                .artistSubscriptionList(subList)
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
                .build();
        salesRepository.save(sales);

        List<ArtistSales> artistSales = new ArrayList<>();
        ArtistSales artistSale = ArtistSales.builder()
                .sales(sales)
                .artist(artist)
                .build();
        artistSalesRepository.save(artistSale);
        artistSales.add(artistSale);

        Hashtag hashtag1 = Hashtag.builder()
                .tag("hashtag1")
                .build();
        hashtagRepository.save(hashtag1);

        SalesHashtag salesHashtag1 = SalesHashtag.builder()
                .hashtag(hashtag1)
                .sales(sales)
                .build();
        salesHashtagRepository.save(salesHashtag1);
        List<SalesHashtag> hashtags = new ArrayList<>();
        hashtags.add(salesHashtag1);

        sales.updateHashtags(hashtags);
    }

    @Test
    @DisplayName("판매글 작성")
    public void createSalesTest() throws Exception {
        //given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        WriteSalesDto writeSalesDto = WriteSalesDto.builder()
                .name("test name2")
                .description("test description2")
                .accountName("test accountName2")
                .accountUser("test accountUser2")
                .accountNumber("test accountNumber2")
                .hashtag(new ArrayList<>())
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 8, 25, 10, 00))
                .namePrice(20000)
                .build();
        String salesBody = objectMapper.writeValueAsString(writeSalesDto);
        MockMultipartFile salesPart = new MockMultipartFile("sale", "", MediaType.APPLICATION_JSON_VALUE, salesBody.getBytes());

        List<CreateProductDto> productDtos = new ArrayList<>();
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("test product1")
                .price(1000)
                .stock(100)
                .build();
        productDtos.add(createProductDto);
        String productBody = objectMapper.writeValueAsString(productDtos);
        MockMultipartFile productPart = new MockMultipartFile("products", "", MediaType.APPLICATION_JSON_VALUE, productBody.getBytes());

        List<CreateDeliveryDto> deliveryDtos = new ArrayList<>();
        CreateDeliveryDto createDeliveryDto = CreateDeliveryDto.builder()
                .name("test delivery1")
                .price(1800)
                .build();
        deliveryDtos.add(createDeliveryDto);
        String deliveryBody = objectMapper.writeValueAsString(deliveryDtos);
        MockMultipartFile deliveryPart = new MockMultipartFile("delivery", "", MediaType.APPLICATION_JSON_VALUE, deliveryBody.getBytes());

        // when
        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId())
                                .file(salesPart)
                                .file(productPart)
                                .file(deliveryPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("판매중인 굿즈/공구 조회")
    public void readOngoingSalesTest() throws Exception {
        MockMultipartFile filter = new MockMultipartFile("filter", "", MediaType.APPLICATION_JSON_VALUE, "name".getBytes());
        MockMultipartFile keyword = new MockMultipartFile("keyword", "", MediaType.APPLICATION_JSON_VALUE, "test name1".getBytes());

        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId() + "/ongoing")
                                .file(filter)
                                .file(keyword)
                                .with(request -> {
                                    request.setMethod("GET");
                                    return request;
                                })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("구독중인 아티스트/팀 굿즈/공구 조회")
    public void readSalesBySubscribedArtistsTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId() + "/subscribedArtists")
                                .param("filter", "name")
                                .param("keyword", "test name1")
                                .with(request -> {
                                    request.setMethod("GET");
                                    return request;
                                })
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("구독, 진행중인 굿즈/공구 조회")
    public void readOngoingSalesBySubscribedArtistsTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId() + "/subscribedArtists/ongoing")
                                .param("filter", "name")
                                .param("keyword", "test name1")
                                .with(request -> {
                                    request.setMethod("GET");
                                    return request;
                                })
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("판매글 목록 조회")
    public void readSalesListTest() throws Exception {
        mockMvc.perform(
                        multipart("/api/v1/sales/" + testCategory.getId())
                                .param("filter", "name")
                                .param("keyword", "test name1")
                                .with(request -> {
                                    request.setMethod("GET");
                                    return request;
                                })
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("판매글 상세조회")
    public void readSalesOneTest() throws Exception {
        mockMvc.perform(get("/api/v1/sales/" + testCategory.getId() + "/" + sales.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("판매글 수정")
    public void updateSalesTest() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        UpdateSalesDto updateSalesDto = UpdateSalesDto.builder()
                .name("updated name")
                .description("updated description")
                .hashtag(new ArrayList<>())
                .accountName("updated accountName")
                .accountUser("updated accountUser")
                .accountNumber("update accountNumber")
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 10, 25, 10, 00))
                .namePrice(25000)
                .build();
        String salesBody = objectMapper.writeValueAsString(updateSalesDto);
        MockMultipartFile salesPart = new MockMultipartFile("sale", "", MediaType.APPLICATION_JSON_VALUE, salesBody.getBytes());

        List<CreateProductDto> productDtos = new ArrayList<>();
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("test product1")
                .price(1000)
                .stock(100)
                .build();
        productDtos.add(createProductDto);
        String productBody = objectMapper.writeValueAsString(productDtos);
        MockMultipartFile productPart = new MockMultipartFile("products", "", MediaType.APPLICATION_JSON_VALUE, productBody.getBytes());

        List<CreateDeliveryDto> deliveryDtos = new ArrayList<>();
        CreateDeliveryDto createDeliveryDto = CreateDeliveryDto.builder()
                .name("test delivery1")
                .price(1800)
                .build();
        deliveryDtos.add(createDeliveryDto);
        String deliveryBody = objectMapper.writeValueAsString(deliveryDtos);
        MockMultipartFile deliveryPart = new MockMultipartFile("delivery", "", MediaType.APPLICATION_JSON_VALUE, deliveryBody.getBytes());

        // when
        mockMvc.perform(
                        multipart("/api/v1/sales/" + sales.getId())
                                .file(salesPart)
                                .file(productPart)
                                .file(deliveryPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                })
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("updated name"));
    }

    @Test
    @DisplayName("판매글 삭제")
    public void deleteSalesTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(delete("/api/v1/sales/" + testCategory.getId() + "/" + sales.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("판매글 찜하기")
    public void likeSalesTest() throws Exception {
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(post("/api/v1/sales/" + sales.getId() + "/like"))
                .andDo(print())
                .andExpect(status().isOk());
    }
}

