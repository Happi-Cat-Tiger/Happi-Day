package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.QArtist;
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
public class ArtistCustomRepositoryImpl implements ArtistCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Artist> findArtistsSlice(Pageable pageable) {
        QArtist qArtist = QArtist.artist;

        List<Artist> artists = queryFactory
                .selectFrom(qArtist)
                .orderBy(qArtist.name.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = artists.size() > pageable.getPageSize();
        List<Artist> content = hasNext ? artists.subList(0, pageable.getPageSize()) : artists;

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
