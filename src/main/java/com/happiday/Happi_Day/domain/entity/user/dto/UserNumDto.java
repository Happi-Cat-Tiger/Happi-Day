package com.happiday.Happi_Day.domain.entity.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserNumDto {
    private String username;
    private String code;

    public UserNumDto(String username, String code) {
        this.username = username;
        this.code = code;
    }
}
