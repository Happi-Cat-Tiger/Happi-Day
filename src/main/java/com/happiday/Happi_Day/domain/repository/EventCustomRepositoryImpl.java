package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.event.Event;
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

import static com.happiday.Happi_Day.domain.entity.event.QEvent.event;
import static com.happiday.Happi_Day.domain.entity.user.QUser.user;

@Repository
@Slf4j
@RequiredArgsConstructor
public class EventCustomRepositoryImpl implements EventCustomRepository{

    private final JPAQueryFactory queryFactory;

    // 이벤트 목록
    public Page<Event> findEventsByFilterAndKeyword(Pageable pageable, String filter, String keyword) {
        List<Event> events = queryFactory
                .selectFrom(event)
                .join(event.user, user).fetchJoin()
                .where(eventSearchFilter(filter, keyword))
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // 카운트
        Long count = queryFactory
                .select(event.count())
                .from(event)
                .where(eventSearchFilter(filter, keyword))
                .fetchOne();

        return new PageImpl<>(events, pageable, count);
    }

    // 진행중인 이벤트 목록
    public Page<Event> findEventsByFilterAndKeywordAndOngoing(Pageable pageable, String filter, String keyword) {

        List<Event> events = queryFactory
                .selectFrom(event)
                .join(event.user, user).fetchJoin()
                .where(ongoingCondition().and(eventSearchFilter(filter, keyword)))
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 카운트
        Long count = queryFactory
                .select(event.count())
                .from(event)
                .where(ongoingCondition().and(eventSearchFilter(filter, keyword)))
                .fetchOne();

        log.info("닉네임 : " + event.user.nickname);

        return new PageImpl<>(events, pageable, count);
    }

    // 내가 구독한 아티스트의 이벤트 목록
    public Page<Event> findEventsByFilterAndKeywordAndSubscribedArtists(Pageable pageable, String filter, String keyword, User loginUser) {

        log.info("이건 !!!! userId : " + loginUser.getId());

        List<Event> events = queryFactory
                .selectFrom(event)
                .where(subscribedArtistsCondition(loginUser)
                        .and(eventSearchFilter(filter, keyword))
                )
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        // 카운트
        Long count = queryFactory
                .select(event.count())
                .from(event)
                .where(subscribedArtistsCondition(loginUser)
                        .and(eventSearchFilter(filter, keyword))
                )
                .fetchOne();

        log.info("닉네임 : " + event.user.nickname);

        return new PageImpl<>(events, pageable, count);
    }

    // 내가 구독한 아티스트의 진행 중인 이벤트 목록
    public Page<Event> findEventsByFilterAndKeywordAndOngoingAndSubscribedArtists(
            Pageable pageable, String filter, String keyword, User loginUser) {


        List<Event> events = queryFactory
                .selectFrom(event)
                .join(event.user, user).fetchJoin()
                .where(ongoingCondition()
                        .and(subscribedArtistsCondition(loginUser))
                        .and(eventSearchFilter(filter, keyword))
                )
                .orderBy(event.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();



        // 카운트
        Long count = queryFactory
                .select(event.count())
                .from(event)
                .where(ongoingCondition()
                        .and(subscribedArtistsCondition(loginUser))
                        .and(eventSearchFilter(filter, keyword))
                )
                .fetchOne();

        log.info("닉네임 : " + event.user.nickname);

        return new PageImpl<>(events, pageable, count);
    }



    // 이벤트 검색 필터링 메서드
    private BooleanExpression eventSearchFilter(String filter, String keyword) {
        log.info("filter!! : " + filter);
        log.info("keyword!! : " + keyword);

        if (StringUtils.hasText(filter)) {
            return switch (filter) {
                case "title" -> event.title.contains(keyword);
                case "username" -> event.user.nickname.contains(keyword);
                case "artistAndTeam" -> event.artistsEventList.any().artist.name.contains(keyword)
                            .or(event.teamsEventList.any().team.name.contains(keyword));
                case "all" -> event.title.contains(keyword)
                            .or(event.user.nickname.contains(keyword))
                            .or(event.artistsEventList.any().artist.name.contains(keyword))
                            .or(event.teamsEventList.any().team.name.contains(keyword));
                default -> null;
            };
        } else {
            return null;
        }
    }

    // 진행중인 이벤트 필터링 메서드
    private BooleanExpression ongoingCondition() {
        return event.startTime.before(LocalDateTime.now())
                .and(event.endTime.after(LocalDateTime.now()));
    }

    // 구독한 아티스트/팀의 이벤트 필터링 메서드
    private BooleanExpression subscribedArtistsCondition(User loginUser) {
        List<Long> artistIds = loginUser.getArtistSubscriptionList().stream()
                .map(ArtistSubscription::getArtist)
                .map(Artist::getId)
                .toList();

        List<Long> teamIds = loginUser.getTeamSubscriptionList().stream()
                .map(TeamSubscription::getTeam)
                .map(Team::getId)
                .toList();

        return event.artistsEventList.any().id.in(artistIds)
                .or(event.teamsEventList.any().id.in(teamIds));

    }

}
