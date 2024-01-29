package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatMessageDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Page<ChatMessageDto> getChatMessages(String username, Long roomId, Pageable pageable) {
        User sender = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Page<ChatMessage> chatMessagePage = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(roomId, pageable);

        List<ChatMessageDto> chatMessageDtoList = chatMessagePage.getContent().stream()
                .map(ChatMessageDto::fromEntity)
                .collect(Collectors.toList());

        return new PageImpl<>(chatMessageDtoList, pageable, chatMessagePage.getTotalElements());
    }

    @Transactional
    public ChatMessageDto sendMessage(String username, Long roomId, ChatMessageDto dto) {
        User sender = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        chatRoom.renewChatRoom();
        chatRoomRepository.save(chatRoom);

        ChatMessageDto payload = ChatMessageDto.fromEntity(chatMessageRepository.save(dto.newEntity(sender, chatRoom)));
        User receiver = chatRoom.getReceiver();
        messagingTemplate.convertAndSend(
                String.format(String.format("/pub/%s", receiver.getId())),
                payload
        );
        messagingTemplate.convertAndSend(
                String.format(String.format("/pub/%s", sender.getId())),
                payload
        );

        return payload;
    }

    @Transactional
    public void readMessage(String username, Long roomId) {
        User sender = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        List<ChatMessage> chatMessageList = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(roomId);
        chatMessageList.stream().peek(chatMessage -> chatMessage.setChecked()).collect(Collectors.toList());
        chatMessageRepository.saveAll(chatMessageList);
    }
}
