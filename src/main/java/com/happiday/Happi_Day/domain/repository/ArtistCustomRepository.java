package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ArtistCustomRepository {
    Slice<Artist> findArtistsSlice(Pageable pageable);
}
