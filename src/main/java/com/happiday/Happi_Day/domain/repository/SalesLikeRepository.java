package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesLike;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SalesLikeRepository extends JpaRepository<SalesLike, Long> {
    Optional<SalesLike> findByUserAndSales(User user, Sales sales);

    List<SalesLike> findBySales(Sales sales);
}
