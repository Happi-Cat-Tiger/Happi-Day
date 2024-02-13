package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamSalesRepository extends JpaRepository<TeamSales, Long> {
    void deleteBySales(Sales sales);
}
