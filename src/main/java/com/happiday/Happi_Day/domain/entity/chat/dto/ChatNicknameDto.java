package com.happiday.Happi_Day.domain.entity.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatNicknameDto {
    private String nickname;

    public ChatNicknameDto(String nickname) {
        this.nickname = nickname;
    }
}
