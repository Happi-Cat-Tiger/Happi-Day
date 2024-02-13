package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesHashtag;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReadOneSalesDto {
    private Long id;
    private String salesCategory;
    private String user;
    private String name;
    private String description;
    private String salesStatus;
    private List<ReadProductDto> products;
    private List<String> artists;
    private List<String> teams;
    private List<String> hashtags;
    private int likeNum;
    private List<String> imageList;
    private List<ReadDeliveryDto> deliveries;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int viewCount;

    public static ReadOneSalesDto fromEntity(Sales sales, List<ReadProductDto> productList){

        List<ReadDeliveryDto> deliveries = sales.getDeliveries() != null ? sales.getDeliveries().stream().map(ReadDeliveryDto::fromEntity).collect(Collectors.toList()) : Collections.emptyList();

        return ReadOneSalesDto.builder()
                .id(sales.getId())
                .salesCategory(sales.getSalesCategory().getName())
                .user(sales.getUsers().getNickname())
                .name(sales.getName())
                .description(sales.getDescription())
                .salesStatus(sales.getSalesStatus().getValue())
                .products(productList)
                .likeNum(sales.getSalesLikes().size())
                .imageList(sales.getImageUrl())
                .artists(sales.getArtistSalesList().stream().map(ArtistSales::getArtist).map(Artist::getName).collect(Collectors.toList()))
                .teams(sales.getTeamSalesList().stream().map(TeamSales::getTeam).map(Team::getName).collect(Collectors.toList()))
                .hashtags(sales.getSalesHashtags().stream().map(SalesHashtag::getHashtag).map(Hashtag::getTag).collect(Collectors.toList()))
                .deliveries(deliveries)
                .startTime(sales.getStartTime())
                .endTime(sales.getEndTime())
                .viewCount(sales.getViewCount())
                .build();
    }
}
