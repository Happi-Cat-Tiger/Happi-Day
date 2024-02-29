package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesHashtagRepository extends JpaRepository<SalesHashtag, Long> {
    void deleteBySales(Sales sales);
}
