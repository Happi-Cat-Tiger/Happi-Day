package com.happiday.Happi_Day.domain.repository.artist;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    private Artist artist;

    @BeforeEach
    public void init() {
        // 아티스트 기본 데이터 생성
        artist = Artist.builder()
                .name("Test Artist")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build();
        artist = artistRepository.save(artist);
    }

    @Test
    @DisplayName("아티스트 생성 및 조회")
    public void createAndFindArtist() {
        // given
        String artistName = "Test Artist";

        // when
        Optional<Artist> foundArtist = artistRepository.findByName(artistName);

        // then
        assertThat(foundArtist.isPresent()).isTrue();
        assertThat(foundArtist.get().getName()).isEqualTo(artistName);
    }

    @Test
    @DisplayName("아티스트 명으로 검색")
    public void findArtistByName() {
        // given
        String artistName = artist.getName();

        // when
        Optional<Artist> foundArtist = artistRepository.findByName(artistName);

        // then
        assertThat(foundArtist.isPresent()).isTrue();
        assertThat(foundArtist.get().getName()).isEqualTo(artistName);
    }

    @Test
    @DisplayName("구독하지 않은 아티스트 조회")
    public void findUnsubscribedArtists() {
        // given
        Long userId = 1L; // 테스트 유저 ID
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Artist> artistsPage = artistRepository.findUnsubscribedArtists(userId, pageRequest);

        // then
        assertThat(artistsPage.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("아티스트 이름으로 존재 여부 확인")
    public void existsByName() {
        // given
        String artistName = "Test Artist";

        // when
        boolean exists = artistRepository.existsByName(artistName);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("아티스트 정보 업데이트")
    public void updateArtist() {
        // given
        Artist updatedArtist = Artist.builder()
                .name("Updated Artist")
                .description("Updated description")
                .build();

        // when
        artist.update(updatedArtist);
        artistRepository.save(artist);

        // then
        Artist foundArtist = artistRepository.findById(artist.getId()).get();
        assertThat(foundArtist.getName()).isEqualTo(updatedArtist.getName());
        assertThat(foundArtist.getDescription()).isEqualTo(updatedArtist.getDescription());
    }

    @Test
    @DisplayName("아티스트 삭제")
    public void deleteArtist() {
        // given
        Long artistId = artist.getId();

        // when
        artistRepository.deleteById(artistId);

        // then
        Optional<Artist> deletedArtist = artistRepository.findById(artistId);
        assertThat(deletedArtist.isPresent()).isFalse();
    }
}
