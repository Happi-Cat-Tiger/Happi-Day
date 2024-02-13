package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistSalesRepository extends JpaRepository<ArtistSales, Long> {
    void deleteBySales(Sales sales);
}
