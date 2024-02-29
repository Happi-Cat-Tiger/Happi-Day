package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
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
    private final ArtistSalesRepository artistSalesRepository;
    private final TeamRepository teamRepository;
    private final TeamSalesRepository teamSalesRepository;
    private final DefaultImageUtils defaultImageUtils;

    public void initSales() {
        User seller = userRepository.findById(2L).orElse(null);
        SalesCategory category1 = salesCategoryRepository.findById(1L).orElse(null);
        SalesCategory category2 = salesCategoryRepository.findById(2L).orElse(null);
        String imageUrl = defaultImageUtils.getDefaultImageUrlSalesThumbnail();

        List<Long> artistsForSales1 = List.of(1L, 2L);
        List<Long> artistsForSales2 = List.of(3L, 4L);
        List<Long> teamsForSales1 = List.of(1L);
        List<Long> teamsForSales2 = List.of(2L);

        Sales sales1 = createSales(seller, category1, "동방신기 티셔츠 팔아요.",
                "동방신기 콘서트 티셔츠 굿즈, 거의 새 것...",
                SalesStatus.ON_SALE, "1234567890",
                LocalDateTime.of(2023, 12, 24, 11, 00),
                LocalDateTime.of(2024, 1, 31, 11, 00), imageUrl);

        Sales sales2 = createSales(seller, category1, "동방신기 티셔츠 팔아요.(판매 기간 종료)",
                "동방신기 콘서트 티셔츠 굿즈, 거의 새 것...",
                SalesStatus.ON_SALE, "1234567890",
                LocalDateTime.of(2023, 12, 24, 11, 00),
                LocalDateTime.of(2023, 12, 31, 11, 00), imageUrl);

        Sales sales3 = createSales(seller, category2, "god 자켓 팔아요.",
                "god 콘서트 자켓 굿즈, 거의 새 것...",
                SalesStatus.ON_SALE, "1234567890",
                LocalDateTime.of(2023, 12, 14, 11, 00),
                LocalDateTime.of(2023, 12, 25, 11, 00), imageUrl);

        List<Sales> salesList = List.of(sales1, sales2, sales3);

        salesList.forEach(sales -> {
            try {
                if (!salesRepository.existsByName(sales.getName())) {
                    salesRepository.save(sales);
                    if (sales != sales3) {
                        linkArtistsToSales(sales, artistsForSales1);
                        linkTeamsToSales(sales, teamsForSales1);
                    } else {
                        linkArtistsToSales(sales, artistsForSales2);
                        linkTeamsToSales(sales, teamsForSales2);
                    }
                }
            } catch (Exception e) {
                log.error("DB Seeder 판매글 저장 중 예외 발생 - 판매글명: {}", sales.getName(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_SALES_SAVE_ERROR);
            }
        });
    }

    private Sales createSales(User seller, SalesCategory category, String title, String description,
                              SalesStatus salesStatus, String account, LocalDateTime startTime, LocalDateTime endTime, String imageUrl) {
        return Sales.builder()
                .users(seller)
                .salesCategory(category)
                .name(title)
                .description(description)
                .salesStatus(salesStatus)
                .account(account)
                .startTime(startTime)
                .endTime(endTime)
                .thumbnailImage(imageUrl)
                .build();
    }

    private void linkArtistsToSales(Sales sales, List<Long> artistIds) {
        artistIds.forEach(artistId -> {
            artistRepository.findById(artistId).ifPresent(artist -> {
                ArtistSales artistSales = ArtistSales.builder()
                        .sales(sales)
                        .artist(artist)
                        .build();
                artistSalesRepository.save(artistSales);
            });
        });
    }

    private void linkTeamsToSales(Sales sales, List<Long> teamIds) {
        teamIds.forEach(teamId -> {
            teamRepository.findById(teamId).ifPresent(team -> {
                TeamSales teamSales = TeamSales.builder()
                        .sales(sales)
                        .team(team)
                        .build();
                teamSalesRepository.save(teamSales);
            });
        });
    }
}
