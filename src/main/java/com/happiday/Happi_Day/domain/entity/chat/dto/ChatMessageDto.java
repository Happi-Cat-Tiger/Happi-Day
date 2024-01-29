package com.happiday.Happi_Day.domain.entity.chat.dto;

import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessageDto {

    private Long id;
    private Long roomId;
    private String sender;
    private String content;
    private LocalDateTime timestamp;

    public static ChatMessageDto fromEntity(ChatMessage entity) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(entity.getId());
        dto.setRoomId(entity.getChatRoom().getId());
        dto.setSender(entity.getSender().getNickname());
        dto.setContent(entity.getContent());
        dto.setTimestamp(entity.getCreatedAt());
        return dto;
    }

    public ChatMessage newEntity(User user, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .checked(false)
                .sender(user)
                .chatRoom(chatRoom)
                .content(content)
                .build();
    }
}

