package com.happiday.Happi_Day.domain.repository.artist;


import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
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

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistSalesRepositoryTest {

    @Autowired
    private ArtistSalesRepository artistSalesRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SalesCategoryRepository salesCategoryRepository;

    private User user;
    private Artist artist;
    private SalesCategory salesCategory;
    private Sales sales;
    private ArtistSales artistSales;

    @BeforeEach
    public void init() {
        // 유저, 아티스트, 판매글 엔티티 생성
        user = userRepository.save(User.builder()
                .username("Test Username")
                .password("Test Password")
                .nickname("Test")
                .realname("Test Realname")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build());
        artist = artistRepository.save(Artist.builder()
                .name("Test Artist")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        salesCategory = salesCategoryRepository.save(SalesCategory.builder()
                .name("굿즈")
                .description("굿즈 카테고리입니다.")
                .build());
        sales = salesRepository.save(Sales.builder()
                .users(user)
                .salesCategory(salesCategory)
                .name("Test Sales Name")
                .namePrice(10000)
                .description("Test Sales Description")
                .salesStatus(SalesStatus.ON_SALE)
                .accountNumber("12345678")
                .accountUser("Test name")
                .accountName("Test Bank")
                .startTime(LocalDateTime.now().plusDays(10))
                .endTime(LocalDateTime.now().plusDays(10).plusHours(4))
                .thumbnailImage("http://example.com/thumbnail.jpg")
                .build());

        // 아티스트 판매 관계 설정 및 저장
        artistSales = ArtistSales.builder()
                .sales(sales)
                .artist(artist)
                .build();
        artistSalesRepository.save(artistSales);
    }

    @Test
    @DisplayName("판매글로 아티스트 판매 삭제")
    public void deleteBySalesTest() {
        // given - 위의 @BeforeEach에서 준비

        // when
        artistSalesRepository.deleteBySales(sales);

        // then
        boolean exists = artistSalesRepository.existsById(artistSales.getId());
        assertThat(exists).isFalse(); // 아티스트 판매 관계가 삭제되었는지 확인
    }
}
