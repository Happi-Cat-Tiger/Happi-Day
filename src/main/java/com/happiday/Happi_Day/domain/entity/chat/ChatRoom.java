package com.happiday.Happi_Day.domain.entity.chat;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "chat_room")
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    private Boolean isSenderDeleted = false;

    private Boolean isReceiverDeleted = false;

    private Boolean open = false;

    public void deleteChatRoomBySender() {
        this.isSenderDeleted = true;
    }

    public void deleteChatRoomByReceiver() {
        this.isReceiverDeleted = true;
    }

    public void renewChatRoom() {
        this.isSenderDeleted = false;
        this.isReceiverDeleted = false;
    }

}
