package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저가 DB에 저장이 잘 되는지 확인")
    void save_user() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Assertions.assertThat(testUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("findByUsername을 통해 유저 찾기")
    void find_by_username() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Optional<User> user = userRepository.findByUsername(testUser.getUsername());
        Assertions.assertThat(testUser.getId()).isEqualTo(user.get().getId());
    }

    @Test
    @DisplayName("findByNickname을 통해 유저 찾기")
    void find_by_nickname() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Optional<User> user = userRepository.findByNickname(testUser.getNickname());
        Assertions.assertThat(testUser.getId()).isEqualTo(user.get().getId());
    }

    @Test
    @DisplayName("existsByUsername을 통해 유저가 존재하는지 확인하기")
    void exists_by_username() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Boolean result = userRepository.existsByUsername(testUser.getUsername());
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByNickname을 통해 유저가 존재하는지 확인하기")
    void exists_by_nickname() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Boolean result = userRepository.existsByNickname(testUser.getNickname());
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("existsByPhone을 통해 유저가 존재하는지 확인하기")
    void exists_by_phone() {

        // given
        User testUser = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);

        // then
        Boolean result = userRepository.existsByPhone(testUser.getPhone());
        Assertions.assertThat(result).isTrue();
    }

    @Test
    @DisplayName("findAllByUsernameNot을 통해 나를 제외한 모든 유저 찾기")
    void find_all_by_username_not() {

        // given
        User testUser = User.builder()
                .username("user@email.com")
                .password("qwer1234")
                .nickname("nick")
                .realname("김철수")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        User user1 = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("nick1")
                .realname("사용자1")
                .phone("01011111111")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        User user2 = User.builder()
                .username("user2email.com")
                .password("qwer1234")
                .nickname("nick2")
                .realname("사용자2")
                .phone("01022222222")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // when
        userRepository.save(testUser);
        userRepository.save(user1);
        userRepository.save(user2);


        // then
        List<User> userList = userRepository.findAllByUsernameNot(testUser.getUsername());
        Assertions.assertThat(userList.size()).isEqualTo(2);
        Assertions.assertThat(userList.stream().allMatch(user -> !user.getUsername().equals(testUser.getUsername())));
    }
}