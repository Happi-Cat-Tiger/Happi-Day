package com.happiday.Happi_Day.domain.repository.artist;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistTeam;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.ArtistTeamRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistTeamRepositoryTest {


    @Autowired
    private ArtistTeamRepository artistTeamRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private TeamRepository teamRepository;

    private Artist artist;
    private Team team;
    private ArtistTeam artistTeam;

    @BeforeEach
    public void init() {
        // 아티스트와 팀 엔티티 생성
        artist = artistRepository.save(Artist.builder()
                .name("Test Artist")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Team Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());

        // 아티스트와 팀 관계 설정
        artistTeam = artistTeamRepository.save(ArtistTeam.builder()
                .artist(artist)
                .team(team)
                .build());
    }

    @Test
    @DisplayName("아티스트로 아티스트-팀 관계 삭제")
    public void deleteByArtistTest() {
        // given - 위의 @BeforeEach에서 준비된 데이터

        // when
        artistTeamRepository.deleteByArtist(artist);

        // then
        boolean exists = artistTeamRepository.findById(artistTeam.getId()).isPresent();
        assertThat(exists).isFalse(); // 아티스트와 관련된 아티스트-팀 관계가 삭제되었는지 확인
    }
}
