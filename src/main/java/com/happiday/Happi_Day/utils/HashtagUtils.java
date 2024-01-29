package com.happiday.Happi_Day.utils;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class HashtagUtils {

    private final ArtistRepository artistRepository;
    private final TeamRepository teamRepository;

    public HashtagUtils(ArtistRepository artistRepository, TeamRepository teamRepository) {
        this.artistRepository = artistRepository;
        this.teamRepository = teamRepository;
    }

    public Triple<List<Artist>, List<Team>, List<Hashtag>> processTags(List<String> hashtagRequests) {
        List<Artist> artists = new ArrayList<>();
        List<Team> teams = new ArrayList<>();
        List<Hashtag> hashtags = new ArrayList<>();

        for (String hashtagRequest : hashtagRequests) {
            Optional<Artist> existingArtist = artistRepository.findByName(hashtagRequest);
            Optional<Team> existingTeam = teamRepository.findByName(hashtagRequest);
            if (existingArtist.isPresent()) {
                artists.add(existingArtist.get());
            } else if (existingTeam.isPresent()) {
                teams.add(existingTeam.get());
            } else {
                hashtags.add(Hashtag.builder().tag(hashtagRequest).build());
            }
        }
        return Triple.of(artists, teams, hashtags);
    }
}


