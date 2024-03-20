package com.happiday.Happi_Day.domain.entity.product.dto;

import com.happiday.Happi_Day.domain.entity.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CreateProductDto {
    private String name;
    private Integer price;
    private Integer stock;

    public Product toEntity(){
        return Product.builder()
                .name(name)
                .price(price)
                .stock(stock)
                .build();
    }
}
