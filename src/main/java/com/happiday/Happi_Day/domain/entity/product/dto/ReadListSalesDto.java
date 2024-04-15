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
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class ReadListSalesDto {
    private Long id;
    private String salesCategory;
    private String name;
    private String user;
    private Integer likeNum;
    private String thumbnailImage;
    private Integer orderNum;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> artists;
    private List<String> teams;
    private List<String> hashtags;
    private int viewCount;

    public static ReadListSalesDto fromEntity(Sales sales){
        return ReadListSalesDto.builder()
                .id(sales.getId())
                .salesCategory(sales.getSalesCategory().getName())
                .name(sales.getName())
                .user(sales.getUsers().getNickname())
                .likeNum(sales.getSalesLikes().size())
                .thumbnailImage(sales.getThumbnailImage())
                .orderNum(sales.getOrders().size())
                .startTime(sales.getStartTime())
                .endTime(sales.getEndTime())
                .artists(sales.getArtistSalesList().stream().map(ArtistSales::getArtist).map(Artist::getName).collect(Collectors.toList()))
                .teams(sales.getTeamSalesList().stream().map(TeamSales::getTeam).map(Team::getName).collect(Collectors.toList()))
                .hashtags(sales.getSalesHashtags().stream().map(SalesHashtag::getHashtag).map(Hashtag::getTag).collect(Collectors.toList()))
                .viewCount(sales.getViewCount())
                .build();
    }
}
