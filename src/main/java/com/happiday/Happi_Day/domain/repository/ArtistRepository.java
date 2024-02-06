package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArtistRepository extends JpaRepository<Artist, Long> {
    Optional<Artist> findByName(String artistName);

    @Query("SELECT a FROM Artist a WHERE a.id NOT IN (SELECT ua.id FROM User u JOIN u.artistSubscriptionList ua WHERE u.id = :userId)")
    Page<Artist> findUnsubscribedArtists(@Param("userId") Long userId, Pageable pageable);

    boolean existsByName(String name);
}
