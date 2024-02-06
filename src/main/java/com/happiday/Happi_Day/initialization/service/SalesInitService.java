package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesInitService {

    private final SalesRepository salesRepository;
    private final UserRepository userRepository;
    private final SalesCategoryRepository salesCategoryRepository;
    private final ArtistRepository artistRepository;
    private final TeamRepository teamRepository;
    private final DefaultImageUtils defaultImageUtils;

    public void initSales() {
        User seller = userRepository.findById(2L).orElse(null);
        SalesCategory category1 = salesCategoryRepository.findById(1L).orElse(null);
        SalesCategory category2 = salesCategoryRepository.findById(2L).orElse(null);
        Artist artist1 = artistRepository.findById(1L).orElse(null);
        Artist artist2 = artistRepository.findById(2L).orElse(null);
        Artist artist3 = artistRepository.findById(3L).orElse(null);
        Artist artist4 = artistRepository.findById(4L).orElse(null);
        Team team1 = teamRepository.findById(1L).orElse(null);
        Team team2 = teamRepository.findById(2L).orElse(null);
        String imageUrl = defaultImageUtils.getDefaultImageUrlSalesThumbnail();


        List<Sales> salesList = List.of(
                Sales.builder()
                        .users(seller)
                        .salesCategory(category1)
                        .name("동방신기 티셔츠 팔아요.")
                        .description("동방신기 콘서트 티셔츠 굿즈, 거의 새 것...")
                        .salesStatus(SalesStatus.ON_SALE)
                        .account("1234567890")
                        .startTime(LocalDateTime.of(2023,12,24,11,00))
                        .endTime(LocalDateTime.of(2024,1,31,11,00))
                        .artists(List.of(artist1, artist2))
                        .teams(List.of(team1))
                        .build(),
                Sales.builder()
                        .users(seller)
                        .salesCategory(category1)
                        .name("동방신기 티셔츠 팔아요.(판매 기간 종료)")
                        .description("동방신기 콘서트 티셔츠 굿즈, 거의 새 것...")
                        .salesStatus(SalesStatus.ON_SALE)
                        .account("1234567890")
                        .startTime(LocalDateTime.of(2023,12,24,11,00))
                        .endTime(LocalDateTime.of(2023,12,31,11,00))
                        .artists(List.of(artist1, artist2))
                        .teams(List.of(team1))
                        .thumbnailImage(imageUrl)
                        .build(),
                Sales.builder()
                        .users(seller)
                        .salesCategory(category2)
                        .name("god 자켓 팔아요.")
                        .description("god 콘서트 자켓 굿즈, 거의 새 것...")
                        .salesStatus(SalesStatus.ON_SALE)
                        .account("1234567890")
                        .startTime(LocalDateTime.of(2023,12,14,11,00))
                        .endTime(LocalDateTime.of(2023,12,25,11,00))
                        .artists(List.of(artist3, artist4))
                        .teams(List.of(team2))
                        .thumbnailImage(imageUrl)
                        .build()
        );

        salesList.forEach(sales -> {
            try {
                if (!salesRepository.existsByName(sales.getName())) {
                    salesRepository.save(sales);
                }
            } catch (Exception e) {
                log.error("DB Seeder 판매글 저장 중 예외 발생 - 판매글명: {}", sales.getName(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_SALES_SAVE_ERROR);
            }
        });
    }
}
