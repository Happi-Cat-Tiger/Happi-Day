package com.happiday.Happi_Day.domain.repository.artist;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.ArtistSubscriptionRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArtistSubscriptionRepositoryTest {

    @Autowired
    private ArtistSubscriptionRepository artistSubscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtistRepository artistRepository;

    private User user;
    private Artist artist;
    private ArtistSubscription artistSubscription;

    @BeforeEach
    public void init() {
        // 사용자와 아티스트 엔티티 생성
        user = userRepository.save(User.builder()
                .username("Test Username")
                .password("Test Password")
                .nickname("Test")
                .realname("Test Realname")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build());
        artist = artistRepository.save(Artist.builder()
                .name("Test Artist")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());

        // 사용자와 아티스트 구독 관계 설정
        artistSubscription = artistSubscriptionRepository.save(ArtistSubscription.builder()
                .user(user)
                .artist(artist)
                .build());
    }

    @Test
    @DisplayName("사용자와 아티스트로 구독 존재 여부 확인")
    public void existsByUserAndArtistTest() {
        // given - 위의 @BeforeEach에서 준비

        // when
        boolean exists = artistSubscriptionRepository.existsByUserAndArtist(user, artist);

        // then
        assertThat(exists).isTrue(); // 사용자와 아티스트의 구독 관계가 존재하는지 확인
    }

    @Test
    @DisplayName("사용자와 아티스트로 구독 조회")
    public void findByUserAndArtistTest() {
        // given - 위의 @BeforeEach에서 준비

        // when
        Optional<ArtistSubscription> foundSubscription = artistSubscriptionRepository.findByUserAndArtist(user, artist);

        // then
        assertThat(foundSubscription.isPresent()).isTrue(); // 구독 정보가 조회되는지 확인
        assertThat(foundSubscription.get()).isEqualTo(artistSubscription); // 조회된 구독 정보가 기대하는 정보와 일치하는지 확인
    }
}
