package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.Product;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import com.happiday.Happi_Day.domain.entity.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SalesDetailResponseDto {
    private Long id;
    private SalesCategory salesCategory;
    private User user;
    private String name;
    private String description;
    private List<String> imageUrl;
    private SalesStatus salesStatus;
    private List<Product> products;
    private List<Artist> artists;
    private List<Team> teams;

    public static SalesDetailResponseDto of(Sales sales) {
        return SalesDetailResponseDto.builder()
                .id(sales.getId())
                .salesCategory(sales.getSalesCategory())
                .user(sales.getUsers())
                .name(sales.getName())
                .description(sales.getDescription())
                .imageUrl(sales.getImageUrl())
                .salesStatus(sales.getSalesStatus())
                .products(sales.getProducts())
                .artists(sales.getArtistSalesList().stream().map(ArtistSales::getArtist).toList())
                .teams(sales.getTeamSalesList().stream().map(TeamSales::getTeam).toList())
                .build();
    }
}
