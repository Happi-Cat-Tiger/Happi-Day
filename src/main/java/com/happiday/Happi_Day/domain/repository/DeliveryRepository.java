package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.product.Delivery;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    List<Delivery> findAllBySales(Sales sales);
    Optional<Delivery> findByName(String name);
    void deleteAllBySales(Sales sales);
    boolean existsByName(String name);
}
