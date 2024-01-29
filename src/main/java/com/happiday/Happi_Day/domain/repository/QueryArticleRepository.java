package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.team.Team;
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

import java.util.List;

import static com.happiday.Happi_Day.domain.entity.article.QArticle.article;
import static com.happiday.Happi_Day.domain.entity.user.QUser.user;

@Slf4j
@RequiredArgsConstructor
@Repository
public class QueryArticleRepository {

    private final JPAQueryFactory queryFactory;

    // 필터링
    public Page<Article> findArticleByFilterAndKeyword(Pageable pageable, String filter, String keyword) {
        List<Article> articleList = queryFactory
                .selectFrom(article)
                .join(article.user, user).fetchJoin()
                .where(articleSearchFilter(filter, keyword))
                .orderBy(article.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(article.count())
                .from(article)
                .where(articleSearchFilter(filter, keyword))
                .fetchOne();

        return new PageImpl<>(articleList, pageable, count);
    }

    // 게시글 검색 필터링 메서드
    private BooleanExpression articleSearchFilter(String filter, String keyword) {
        if (StringUtils.hasText(filter)) {
            return switch (filter) {
                case "name" -> article.title.contains(keyword);
                case "username" -> article.user.nickname.contains(keyword);
                default -> null;
            };
        } else {
            return null;
        }
    }

    public Page<Article> findArticleByFilterAndKeywordAndSubscribedArtists(
            Pageable pageable, String filter, String keyword, User loginUser
    ){
        List<Article> articleList = queryFactory
                .selectFrom(article)
                .where(subscribedArtistsCondition(loginUser)
                        .and(articleSearchFilter(filter, keyword))
                )
                .orderBy(article.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = queryFactory
                .select(article.count())
                .from(article)
                .where(subscribedArtistsCondition(loginUser)
                        .and(articleSearchFilter(filter, keyword)))
                .fetchOne();

        return new PageImpl<>(articleList, pageable, count);
    }

    private BooleanExpression subscribedArtistsCondition(User loginUser) {
        List<Long> artistIds = loginUser.getSubscribedArtists().stream()
                .map(Artist::getId)
                .toList();

        List<Long> teamIds = loginUser.getSubscribedTeams().stream()
                .map(Team::getId)
                .toList();

        return article.artists.any().id.in(artistIds)
                .or(article.teams.any().id.in(teamIds));

    }
}
