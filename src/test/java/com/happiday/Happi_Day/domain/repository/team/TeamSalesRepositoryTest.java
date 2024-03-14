package com.happiday.Happi_Day.domain.repository.team;

import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesCategory;
import com.happiday.Happi_Day.domain.entity.product.SalesStatus;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamSalesRepositoryTest {

    @Autowired
    private TeamSalesRepository teamSalesRepository;

    @Autowired
    private SalesCategoryRepository salesCategoryRepository;

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    private SalesCategory salesCategory;
    private Sales sales;
    private Team team;
    private TeamSales teamSales;
    private User user;

    @BeforeEach
    public void init() {
        // 판매 정보와 팀 엔티티 준비
        user = userRepository.save(User.builder()
                .username("Test Username")
                .password("Test Password")
                .nickname("Test")
                .realname("Test Realname")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build());
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());
        salesCategory = salesCategoryRepository.save(SalesCategory.builder()
                .name("Test SalesCategory")
                .description("Test Description")
                .build());
        sales = salesRepository.save(Sales.builder()
                .users(user)
                .salesCategory(salesCategory)
                .name("Test Sales Name")
                .description("Test Sales Description")
                .salesStatus(SalesStatus.ON_SALE)
                .account("Test Account")
                .startTime(LocalDateTime.now().plusDays(10))
                .endTime(LocalDateTime.now().plusDays(10).plusHours(4))
                .thumbnailImage("http://example.com/thumbnail.jpg")
                .build());

        // 팀 판매 관계 설정
        teamSales = TeamSales.builder()
                .sales(sales)
                .team(team)
                .build();
        teamSalesRepository.save(teamSales);
    }

    @Test
    @DisplayName("판매 정보로 팀 판매 삭제")
    public void deleteBySalesTest() {
        // when
        teamSalesRepository.deleteBySales(sales);

        // then
        boolean exists = teamSalesRepository.existsById(teamSales.getId());
        assertThat(exists).isFalse(); // 팀 판매가 삭제되었는지 확인
    }
}
