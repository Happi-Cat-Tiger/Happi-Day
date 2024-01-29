package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SalesListResponseDto {
    private Long id;
    private String categoryName;
    private String userNickName;
    private String name;
    private SalesStatus salesStatus;

    public static SalesListResponseDto of(Sales sales) {
        return SalesListResponseDto.builder()
                .id(sales.getId())
                .categoryName(sales.getSalesCategory().getName())
                .userNickName(sales.getUsers().getNickname())
                .name(sales.getName())
                .salesStatus(sales.getSalesStatus())
                .build();
    }
}
