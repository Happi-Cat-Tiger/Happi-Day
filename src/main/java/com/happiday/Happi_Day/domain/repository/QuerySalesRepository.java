package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.happiday.Happi_Day.domain.entity.product.QSales.sales;
import static com.happiday.Happi_Day.domain.entity.user.QUser.user;

@Slf4j
@RequiredArgsConstructor
@Repository
public class QuerySalesRepository {

    private final JPAQueryFactory queryFactory;

    // 필터링
    public Page<Sales> findSalesByFilterAndKeyword(Pageable pageable,Long categoryId, String filter, String keyword) {
        List<Sales> salesList = queryFactory
                .selectFrom(sales)
                .join(sales.users, user).fetchJoin()
                .where(salesSearchFilter(filter, keyword),
                        sales.salesCategory.id.eq(categoryId))
                .orderBy(sales.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sales.count())
                .from(sales)
                .where(salesSearchFilter(filter, keyword))
                .fetchOne();

        return new PageImpl<>(salesList, pageable, count);
    }


    // 진행중인 공구/굿즈 목록
    public Page<Sales> findSalesByFilterAndKeywordOngoing(Pageable pageable, String filter, String keyword) {
        List<Sales> salesList = queryFactory
                .selectFrom(sales)
                .join(sales.users, user).fetchJoin()
                .where(ongoingCondition().and(salesSearchFilter(filter, keyword)))
                .orderBy(sales.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sales.count())
                .from(sales)
                .where(ongoingCondition().and(salesSearchFilter(filter, keyword)))
                .fetchOne();

        return new PageImpl<>(salesList, pageable, count);
    }

    // 공구/굿즈 검색 필터링 메서드
    private BooleanExpression salesSearchFilter(String filter, String keyword) {
        if (StringUtils.hasText(filter)) {
            return switch (filter) {
                case "name" -> sales.name.contains(keyword);
                case "username" -> sales.users.nickname.contains(keyword);
                default -> null;
            };
        } else {
            return null;
        }
    }

    public Page<Sales> findSalesByFilterAndKeywordAndSubscribedArtists(
            Pageable pageable, String filter, String keyword, User loginUser
    ){
        List<Sales> salesList = queryFactory
                .selectFrom(sales)
                .where(subscribedArtistsCondition(loginUser)
                        .and(salesSearchFilter(filter, keyword))
                )
                .orderBy(sales.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sales.count())
                .from(sales)
                .where(subscribedArtistsCondition(loginUser)
                        .and(salesSearchFilter(filter, keyword)))
                .fetchOne();

        return new PageImpl<>(salesList, pageable, count);
    }

    public Page<Sales> findSalesByFilterAndKeywordAndOngoingAndSubscribedArtists(
            Pageable pageable, String filter, String keyword, User loginUser
    ){
        List<Sales> salesList = queryFactory
                .selectFrom(sales)
                .join(sales.users, user).fetchJoin()
                .where(ongoingCondition()
                        .and(subscribedArtistsCondition(loginUser))
                        .and(salesSearchFilter(filter, keyword))
                )
                .orderBy(sales.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(sales.count())
                .from(sales)
                .where(ongoingCondition()
                        .and(subscribedArtistsCondition(loginUser))
                        .and(salesSearchFilter(filter, keyword)))
                .fetchOne();

        return new PageImpl<>(salesList, pageable, count);
    }


    private BooleanExpression ongoingCondition() {
        return sales.startTime.before(LocalDateTime.now())
                .and(sales.endTime.after(LocalDateTime.now()));
    }

    private BooleanExpression subscribedArtistsCondition(User loginUser) {
        List<Long> artistIds = loginUser.getArtistSubscriptionList().stream()
                .map(ArtistSubscription::getArtist)
                .map(Artist::getId)
                .toList();

        List<Long> teamIds = loginUser.getTeamSubscriptionList().stream()
                .map(TeamSubscription::getTeam)
                .map(Team::getId)
                .toList();

        return sales.artists.any().id.in(artistIds)
                .or(sales.teams.any().id.in(teamIds));

    }
}
