package com.happiday.Happi_Day.domain.entity.user;

import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserTest {

    @Autowired UserRepository userRepository;

    @Test
    @DisplayName("유저 엔티티 생성 테스트")
    void user() {
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
        userRepository.save(testUser);

        Optional<User> user = userRepository.findByUsername(testUser.getUsername());

        Assertions.assertThat(testUser.getId()).isEqualTo(user.get().getId());
    }
}