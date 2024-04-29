package com.happiday.Happi_Day.domain.service.sales;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.product.dto.*;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.domain.service.SalesService;
import jakarta.persistence.EntityManager;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SalesServiceTest {
    @Autowired
    SalesCategoryRepository salesCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SalesRepository salesRepository;

    @Autowired
    HashtagRepository hashtagRepository;

    @Autowired
    SalesHashtagRepository salesHashtagRepository;

    @Autowired
    SalesService salesService;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistSubscriptionRepository artistSubscriptionRepository;

    @Autowired
    ArtistSalesRepository artistSalesRepository;

    @Autowired
    SalesLikeRepository salesLikeRepository;

    @Autowired
    private EntityManager entityManager;

    private User testUser;
    private Sales sales;
    private SalesCategory testCategory;
    private MultipartFile thumbnailImage;
    private List<MultipartFile> imageList;

    @BeforeEach
    void beforeEach() {
        // 카테고리 생성
        testCategory = SalesCategory.builder()
                .name("test Category")
                .build();
        salesCategoryRepository.save(testCategory);

        // 아티스트 생성
        List<Artist> artists = new ArrayList<>();
        Artist artist = Artist.builder()
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
        testUser = userRepository.save(testUser);

        // 썸네일 이미지 생성
        thumbnailImage = new MockMultipartFile("thumbnailImage", "thumbnailImage.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());

        // 이미지 생성
        imageList = new ArrayList<>();
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
    @DisplayName("판매글 생성")
    public void createSalesTest() {
        // given
        List<String> hashtags = new ArrayList<>();
        hashtags.add("hashtag1");
        hashtags.add("hashtag2");

        List<CreateProductDto> productDto = new ArrayList<>();
        CreateProductDto createProductDto = CreateProductDto.builder()
                .name("test product1")
                .price(1000)
                .stock(100)
                .build();
        productDto.add(createProductDto);

        List<CreateDeliveryDto> deliveryDto = new ArrayList<>();
        CreateDeliveryDto createDeliveryDto = CreateDeliveryDto.builder()
                .name("test delivery")
                .price(1800)
                .build();
        deliveryDto.add(createDeliveryDto);

        WriteSalesDto salesDto = WriteSalesDto.builder()
                .name("test name2")
                .namePrice(1000)
                .description("test description2")
                .accountName("test accountName2")
                .accountUser("test accountUser2")
                .accountNumber("test accountNumber2")
                .hashtag(hashtags)
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 8, 1, 10, 00))
                .build();

        // when
        salesService.createSales(testCategory.getId(), salesDto, productDto, deliveryDto, thumbnailImage, imageList, testUser.getUsername());

        // then
        salesRepository.findById(2L).ifPresent(sales -> {
            assertThat(sales.getName()).isEqualTo("test name2");
        });
    }

    @Test
    @DisplayName("진행중인 판매글 조회")
    public void readOngoingSalesTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "name2";

        // when
        Page<ReadListSalesDto> readListSalesWithFilter = salesService.readOngoingSales(testCategory.getId(), pageable, filter, keyword);
        Page<ReadListSalesDto> readListSales = salesService.readOngoingSales(testCategory.getId(), pageable, null, null);

        // then
        assertThat(readListSalesWithFilter.getContent().size()).isEqualTo(0);

        assertThat(readListSales).isNotNull();
        assertThat(readListSales.getContent().get(0).getName()).isEqualTo("test name1");
    }

    @Test
    @DisplayName("판매글 목록 조회")
    public void readSalesListTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "name2";

        // when
        Page<ReadListSalesDto> readListSalesWithFilter = salesService.readSalesList(testCategory.getId(), pageable, filter, keyword);
        Page<ReadListSalesDto> readListSales = salesService.readOngoingSales(testCategory.getId(), pageable, null, null);

        // then
        assertThat(readListSalesWithFilter.getContent().size()).isEqualTo(0);

        assertThat(readListSales).isNotNull();
        assertThat(readListSales.getContent().get(0).getName()).isEqualTo("test name1");
    }

    @Test
    @DisplayName("구독중인 아티스트의 굿즈/공구 리스트 조회")
    public void readSalesBySubscribedArtistsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "name1";

        // when
        Page<ReadListSalesDto> readListSalesBySubscribedArtists = salesService.readSalesBySubscribedArtists(pageable, testCategory.getId(), filter, keyword, testUser.getUsername());

        // then
        assertThat(readListSalesBySubscribedArtists.getContent().get(0).getName()).isEqualTo("test name1");

    }

    @Test
    @DisplayName("구독, 진행중인 굿즈/공구 리스트 조회")
    public void readOngoingSalesBySubscribedArtistsTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "name1";

        // when
        Page<ReadListSalesDto> readListSalesBySubscribedArtists = salesService.readOngoingSalesBySubscribedArtists(pageable, testCategory.getId(), filter, keyword, testUser.getUsername());

        // then
        assertThat(readListSalesBySubscribedArtists.getContent().get(0).getName()).isEqualTo("test name1");
    }

    @Test
    @DisplayName("판매글 상세 조회")
    public void readSalesOneTest() {
        // given
        String clientAddress = "192.168.0.1";

        // when
        ReadOneSalesDto readOneSales = salesService.readSalesOne(clientAddress, testCategory.getId(), sales.getId());

        // then
        assertThat(readOneSales.getName()).isEqualTo("test name1");
        assertThat(readOneSales.getDescription()).isEqualTo("test description1");
    }

    @Test
    @DisplayName("판매글 수정")
    public void updateSalesTest() {
        // given
        List<String> hashtags = new ArrayList<>();
        hashtags.add("updated hashtag1");
        hashtags.add("updated hashtag2");

        List<CreateProductDto> productList = new ArrayList<>();
        CreateProductDto productdto = CreateProductDto.builder()
                .name("updated test product1")
                .price(2000)
                .stock(200)
                .build();
        productList.add(productdto);

        List<CreateDeliveryDto> deliveyList = new ArrayList<>();
        CreateDeliveryDto createDeliveryDto = CreateDeliveryDto.builder()
                .name("updated test delivery")
                .price(1800)
                .build();
        deliveyList.add(createDeliveryDto);

        UpdateSalesDto salesDto = UpdateSalesDto.builder()
                .name("updated test name")
                .description("updated test description")
                .hashtag(hashtags)
                .accountName("updated accountName")
                .accountNumber("updated accountNumber")
                .accountUser("updated accountUser")
                .startTime(LocalDateTime.of(2024, 4, 1, 10, 00))
                .endTime(LocalDateTime.of(2024, 8, 25, 10, 00))
                .namePrice(20000)
                .build();

        // when
        ReadOneSalesDto readOneSales = salesService.updateSales(sales.getId(), salesDto, productList, deliveyList, thumbnailImage, imageList, testUser.getUsername());

        // then
        assertThat(readOneSales.getName()).isEqualTo("updated test name");
        assertThat(readOneSales.getDescription()).isEqualTo("updated test description");
    }

    @Test
    @DisplayName("판매글 삭제")
    public void deleteSalesTest() {
        // when
        salesService.deleteSales(testCategory.getId(), sales.getId(), testUser.getUsername());

        // then
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("판매글 찜하기")
    public void likeSalesTest() {
        // when
        salesService.likeSales(sales.getId(), testUser.getUsername());

        // then
        List<SalesLike> salesLikes = salesLikeRepository.findBySales(sales);
        assertThat(salesLikes.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("판매글 조회수 증가")
    public void increaseViewCountTest() {
        // given
        String clientAddress = "192.168.0.1";
        int beforeIncrease = sales.getViewCount();

        // when
        salesService.increaseViewCount(clientAddress, sales.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        Sales updateSales = salesRepository.findById(sales.getId()).get();
        assertThat(updateSales.getViewCount()).isEqualTo(beforeIncrease + 1);
    }

}
