package com.happiday.Happi_Day.domain.entity.chat.dto;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomResponse {
    Long id;
    String receiver;
    private Integer messageCount;
    private Integer messageNotRead;

    public static ChatRoomResponse fromEntity(ChatRoom chatRoom, User sender) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .receiver(chatRoom.getSender().equals(sender) ? chatRoom.getReceiver().getNickname() : chatRoom.getSender().getNickname())
                .messageCount(chatRoom.getChatMessages() == null ? 0 : chatRoom.getChatMessages().size())
                .messageNotRead(chatRoom.getChatMessages() == null ? 0 : chatRoom.getChatMessages().stream().reduce(
                        0, (sum, message) -> sum + ((!message.getChecked() && !message.getSender().equals(sender)) ? 1: 0), Integer::sum
                ))
                .build();
    }
}
