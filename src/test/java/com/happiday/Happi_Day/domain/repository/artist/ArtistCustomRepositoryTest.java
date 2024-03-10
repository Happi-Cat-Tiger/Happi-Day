package com.happiday.Happi_Day.domain.repository.artist;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.repository.ArtistCustomRepository;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistCustomRepositoryTest {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistCustomRepository artistCustomRepository;

    @BeforeEach
    public void init() {
        // 테스트 데이터 준비
        artistRepository.save(Artist.builder()
                .name("Test Artist 1")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        artistRepository.save(Artist.builder()
                .name("Test Artist 2")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
        artistRepository.save(Artist.builder()
                .name("Test Artist 3")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());
    }

    @Test
    @DisplayName("아티스트 슬라이스 조회")
    public void findArtistsSliceTest() {
        // given
        Pageable pageable = PageRequest.of(0, 2); // 첫 페이지, 페이지 당 아이템 2개

        // when
        Slice<Artist> artistSlice = artistCustomRepository.findArtistsSlice(pageable);

        // then
        assertThat(artistSlice.getContent()).hasSize(2); // 요청한 페이지 크기만큼 아티스트 로드
        assertThat(artistSlice.hasNext()).isTrue(); // 더 로드할 아티스트가 있어야 함
    }
}
