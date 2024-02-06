package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

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
                .viewCount(sales.getViewCount())
                .build();
    }
}
