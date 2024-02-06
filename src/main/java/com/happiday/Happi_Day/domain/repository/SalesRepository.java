package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SalesRepository extends JpaRepository<Sales, Long> {
    Page<Sales> findAllBySalesCategory(SalesCategory category, Pageable pageable);

    Page<Sales> findAllByUsers(User user, Pageable pageable);

    Page<Sales> findAllBySalesLikesUserContains(User user, Pageable pageable);

    boolean existsByName(String name);

    @Modifying
    @Query("update Sales p set p.viewCount = p.viewCount + 1 where p.id = :id")
    int increaseViewCount(@Param("id") Long salesId);

}
