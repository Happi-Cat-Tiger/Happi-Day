package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    ChatRoom findBySender(User sender);
    ChatRoom findBySenderAndReceiver(User sender, User receiver);
    List<ChatRoom> findAllBySenderAndIsSenderDeletedFalse(User sender);
    List<ChatRoom> findAllByReceiverAndIsReceiverDeletedFalse(User receiver);
}
