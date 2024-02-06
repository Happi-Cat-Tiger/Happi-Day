package com.happiday.Happi_Day.domain.entity.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserRegisterDto {
    private String username;
    private String password;
    private String nickname;
    private String realname;
    private String phone;
}
