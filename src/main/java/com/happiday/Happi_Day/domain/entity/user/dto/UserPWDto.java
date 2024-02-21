package com.happiday.Happi_Day.domain.entity.user.dto;

import lombok.Getter;

@Getter
public class UserPWDto {
    private String password;

    public UserPWDto() {
    }

    public UserPWDto(String password) {
        this.password = password;
    }
}
