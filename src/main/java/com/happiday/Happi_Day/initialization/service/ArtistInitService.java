package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistTeam;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.ArtistTeamRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtistInitService {

    private final ArtistRepository artistRepository;
    private final TeamRepository teamRepository;
    private final ArtistTeamRepository artistTeamRepository;
    private final DefaultImageUtils defaultImageUtils;

    public void initArtists() {
        String imageUrl = defaultImageUtils.getDefaultImageUrlTeamArtistProfile();

        Artist artist1 = createArtist("유노윤호", "유노윤호입니다.", imageUrl);
        Artist artist2 = createArtist("시아준수", "시아준수입니다.", imageUrl);
        Artist artist3 = createArtist("박준형", "박준형입니다.", imageUrl);
        Artist artist4 = createArtist("윤계상", "윤계상입니다.", imageUrl);

        List<Long> teamIdsForArtist1 = List.of(1L);
        List<Long> teamIdsForArtist2 = List.of(2L);

        List<Artist> artists = List.of(artist1, artist2, artist3, artist4);

        artists.forEach(artist -> {
            try {
                if (!artistRepository.existsByName(artist.getName())) {
                    // 아티스트 및 팀 연결
                    artistRepository.save(artist);
                    if (artist == artist1 || artist == artist2) {
                        linkTeamsToArtist(artist, teamIdsForArtist1);
                    } else {
                        linkTeamsToArtist(artist, teamIdsForArtist2);
                    }
                }
            } catch (Exception e) {
                log.error("DB Seeder 아티스트 저장 중 예외 발생 - 아티스트명: {}", artist.getName(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_ARTIST_SAVE_ERROR);
            }
        });
    }

    private Artist createArtist(String name, String description, String imageUrl) {
        return Artist.builder()
                .name(name)
                .description(description)
                .profileUrl(imageUrl)
                .build();
    }

    private void linkTeamsToArtist(Artist artist, List<Long> teamIds) {
        teamIds.forEach(teamId -> {
            teamRepository.findById(teamId).ifPresent(team -> {
                ArtistTeam artistTeam = ArtistTeam.builder()
                        .artist(artist)
                        .team(team)
                        .build();
                artistTeamRepository.save(artistTeam);
            });
        });
    }
}
