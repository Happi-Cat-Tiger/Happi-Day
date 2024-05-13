package com.happiday.Happi_Day.domain.service;


import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatRoomResponse;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public Long createChatRoom(String nickname, String username) {
        User receiver = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        User sender = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (sender.equals(receiver))
            throw new CustomException(ErrorCode.CHATROOM_NOT_CREATED);

        ChatRoom chatRoom1 = chatRoomRepository.findBySenderAndReceiver(sender, receiver);
        ChatRoom chatRoom2 = chatRoomRepository.findBySenderAndReceiver(receiver, sender);

        ChatRoom chatRoom = null;

        if (chatRoom1 != null) {
            chatRoom1.renewChatRoom();
            chatRoomRepository.save(chatRoom1);
            return chatRoom1.getId();
        } else if (chatRoom2 != null) {
            chatRoom2.renewChatRoom();
            chatRoomRepository.save(chatRoom2);
            return chatRoom2.getId();
        } else {
            chatRoom = ChatRoom.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .isSenderDeleted(false)
                    .isReceiverDeleted(false)
                    .open(false)
                    .chatMessages(new ArrayList<>())
                    .build();
        }

        ChatRoom saveChatRoom = chatRoomRepository.save(chatRoom);
        return saveChatRoom.getId();
    }

    public List<ChatRoomResponse> findChatRooms(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<ChatRoom> senderChatRooms = chatRoomRepository.findAllBySenderAndIsSenderDeletedFalse(user);
        List<ChatRoom> receiverChatRooms = chatRoomRepository.findAllByReceiverAndIsReceiverDeletedFalse(user);
        List<ChatRoom> chatRooms = Stream.concat(senderChatRooms.stream(), receiverChatRooms.stream())
                .collect(Collectors.toList());

        return chatRooms.stream().map(room -> ChatRoomResponse.fromEntity(room, user)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteChatRoom(String username, Long roomId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        if (chatRoom.getSender().equals(user)) {
            chatRoom.deleteChatRoomBySender();
        }
        else chatRoom.deleteChatRoomByReceiver();
        chatRoomRepository.save(chatRoom);
    }

}
