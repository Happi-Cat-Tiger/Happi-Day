package com.happiday.Happi_Day.domain.service.chat;

import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatMessageDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.ChatService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private User testUser1;

    private User testUser2;

    private ChatRoom chatRoom;

    @BeforeEach
    public void init() {
        testUser1 = User.builder()
                .username("test1@email.com")
                .password("qwer1234")
                .nickname("닉네임1")
                .realname("테스트1")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(testUser1);

        testUser2 = User.builder()
                .username("test2@email.com")
                .password("qwer1234")
                .nickname("닉네임2")
                .realname("테스트2")
                .phone("01087654321")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(testUser2);

        chatRoom = ChatRoom.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);
    }

        @Test
    void 채팅내역가져오기() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(testUser1)
                .content("안녕하세요")
                .checked(false)
                .build();
        chatMessageRepository.save(chatMessage);
        ChatMessageDto dto = chatService.sendMessage(testUser1.getUsername(), chatRoom.getId(), ChatMessageDto.fromEntity(chatMessage));

        // when
        Page<ChatMessageDto> result = chatService.getChatMessages(testUser1.getUsername(), chatRoom.getId(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isNotNull();
    }

    @Test
    void 채팅하기() {
        // given
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(testUser1)
                .content("안녕하세요")
                .checked(false)
                .build();
        chatMessageRepository.save(chatMessage);

        // when
        ChatMessageDto result = chatService.sendMessage(testUser1.getUsername(), chatRoom.getId(), ChatMessageDto.fromEntity(chatMessage));

        // then
        Assertions.assertThat(result.getContent()).isEqualTo("안녕하세요");
        Assertions.assertThat(result.getRoomId()).isEqualTo(chatRoom.getId());
    }

    @Test
    void 채팅읽음처리() {
        // given
        Pageable pageable = PageRequest.of(0, 20);
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(testUser1)
                .content("안녕하세요")
                .checked(false)
                .build();
        chatMessageRepository.save(chatMessage);
        ChatMessageDto dto = chatService.sendMessage(testUser1.getUsername(), chatRoom.getId(), ChatMessageDto.fromEntity(chatMessage));

        // when
        chatService.readMessage(testUser1.getUsername(), chatRoom.getId());

        // then
        List<ChatMessage> result = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(chatRoom.getId());
        Assertions.assertThat(result.stream().allMatch(m -> m.getChecked().equals(true))).isTrue();
    }
}