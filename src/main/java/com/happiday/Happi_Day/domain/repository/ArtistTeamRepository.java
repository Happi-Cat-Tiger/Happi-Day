package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistTeam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistTeamRepository extends JpaRepository<ArtistTeam, Long> {
    void deleteByArtist(Artist artist);
}
