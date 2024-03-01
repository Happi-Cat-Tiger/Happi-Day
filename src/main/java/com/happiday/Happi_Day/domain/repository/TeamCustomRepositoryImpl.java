package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.team.QTeam;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TeamCustomRepositoryImpl implements TeamCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Team> findTeamsSlice(Pageable pageable) {
        QTeam qTeam = QTeam.team;

        List<Team> teams = queryFactory
                .selectFrom(qTeam)
                .orderBy(qTeam.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = teams.size() > pageable.getPageSize();
        List<Team> content = hasNext ? teams.subList(0, pageable.getPageSize()) : teams;

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
