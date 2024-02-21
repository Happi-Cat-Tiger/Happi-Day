package com.happiday.Happi_Day.domain.entity.user;

import com.happiday.Happi_Day.domain.entity.article.ArticleLike;
import com.happiday.Happi_Day.domain.entity.event.EventLike;
import com.happiday.Happi_Day.domain.entity.product.SalesLike;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class UserTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void testUserEntityAndMappings() {
        // given
        User user = User.builder()
                .username("testUser")
                .password("testPassword")
                .nickname("testNickname")
                .realname("testRealname")
                .phone("testPhone")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        // Add sample ArticleLike
        ArticleLike articleLike = ArticleLike.builder().user(user).build();
        user.getArticleLikes().add(articleLike);

        // Add sample SalesLike
        SalesLike salesLike = SalesLike.builder().user(user).build();
        user.getSalesLikes().add(salesLike);

        // Add sample EventLike
        EventLike eventLike = EventLike.builder().user(user).build();
        user.getEventLikes().add(eventLike);

        // when
        userRepository.save(user);
        User savedUser = userRepository.findById(user.getId()).orElse(null);

        // then
        assertNotNull(savedUser);
        assertEquals("testUser", savedUser.getUsername());
        assertEquals(1, savedUser.getArticleLikes().size());
        assertEquals(1, savedUser.getSalesLikes().size());
        assertEquals(1, savedUser.getEventLikes().size());
    }

    @Test
    void testUserRepositoryFindAllByUsernameNot() {
        // given
        User user1 = User.builder()
                .username("user1")
                .password("pass1")
                .nickname("nick1")
                .realname("real1")
                .phone("phone1")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        User user2 = User.builder()
                .username("user2")
                .password("pass2")
                .nickname("nick2")
                .realname("real2")
                .phone("phone2")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        // when
        List<User> userList = userRepository.findAllByUsernameNot("user1");

        // then
        assertNotNull(userList);
        assertEquals(1, userList.size());
        assertEquals("user2", userList.get(0).getUsername());
    }
}