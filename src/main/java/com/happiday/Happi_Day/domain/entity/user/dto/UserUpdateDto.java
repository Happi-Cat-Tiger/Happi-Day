package com.happiday.Happi_Day.domain.entity.user.dto;

import com.happiday.Happi_Day.domain.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDto {
    private String password;
    private String nickname;
    private String phone;

    public User toEntity(User exUser) {
        return User.builder()
                .password(password != null ? password : exUser.getPassword())
                .nickname(nickname != null ? nickname : exUser.getNickname())
                .phone(phone != null ? phone : exUser.getPhone())
                .build();
    }
}
