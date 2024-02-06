package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistSubscriptionRepository extends JpaRepository<ArtistSubscription, Long> {
    boolean existsByUserAndArtist(User user, Artist artist);
    Optional<ArtistSubscription> findByUserAndArtist(User user, Artist artist);
}
