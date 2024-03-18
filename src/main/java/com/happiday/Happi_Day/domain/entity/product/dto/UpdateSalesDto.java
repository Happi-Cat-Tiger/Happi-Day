package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class UpdateSalesDto {
    private String name;
    private String description;
    private List<String> hashtag;
    private String status;
    private String accountName;
    private String accountUser;
    private String accountNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer namePrice;

    public Sales toEntity() {
        return Sales.builder()
                .name(name)
                .namePrice(namePrice)
                .description(description)
                .accountUser(accountUser)
                .accountName(accountName)
                .accountNumber(accountNumber)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }
}
