package com.happiday.Happi_Day.domain.entity.user.dto;

import lombok.Getter;

@Getter
public class UserFindDto {
    private String realname; // 실명
    private String username; // 이메일

    public UserFindDto(String realname, String username) {
        this.realname = realname;
        this.username = username;
    }
}
