package com.happiday.Happi_Day.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtTokenDto {
    private String token;

    public JwtTokenDto(String token) {
        this.token = token;
    }
}
