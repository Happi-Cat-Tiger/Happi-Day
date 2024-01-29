package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByNickname(String nickname);

    boolean existsByUsername(String username);

    boolean existsByNickname(String nickname);

    boolean existsByPhone(String phone);

    List<User> findAllByUsernameNot(String username);
}
