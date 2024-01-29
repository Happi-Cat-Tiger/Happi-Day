package com.happiday.Happi_Day.domain.entity.chat.dto;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDto{

    private Long id;
    private User sender;
    private User receiver;
    private Boolean open;
    private Boolean owner;
    private Integer messageCount;
    private Integer messageNotRead;

    public ChatRoomDto fromEntity(ChatRoom chatRoom, User sender, User receiver) {
        ChatRoomDto dto = new ChatRoomDto();
        dto.setId(chatRoom.getId());
        dto.setSender(sender);
        dto.setReceiver(receiver);
//        dto.setOwner(!chatRoom.getSender().equals(sender));
        dto.setOpen(chatRoom.getOpen());
        dto.setMessageCount(chatRoom.getChatMessages() == null ? 0 : chatRoom.getChatMessages().size());
        dto.setMessageNotRead(chatRoom.getChatMessages() == null ? 0 : chatRoom.getChatMessages().stream().reduce(
                0, (sum, message) -> sum + ((!message.getChecked() && !message.getSender().equals(sender)) ? 1: 0), Integer::sum
        ));
        return dto;
    }
}
