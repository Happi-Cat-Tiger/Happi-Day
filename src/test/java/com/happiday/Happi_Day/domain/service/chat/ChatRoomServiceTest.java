package com.happiday.Happi_Day.domain.service.chat;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatRoomResponse;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.domain.service.ChatRoomService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ChatRoomServiceTest {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    private User testUser1;

    private User testUser2;

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
    }

    @Test
    void 채팅방생성() {
        // given

        // when
        Long result = chatRoomService.createChatRoom("닉네임2", testUser1.getUsername());

        // then
        ChatRoom chatRoom1 = chatRoomRepository.findBySenderAndReceiver(testUser1, testUser2);
        Assertions.assertThat(result).isEqualTo(chatRoom1.getId());
    }

    @Test
    void 채팅방생성_이미생성된방이있을때_sender기준() {
        // given
        Long roomId = chatRoomService.createChatRoom("닉네임2", testUser1.getUsername());
        chatRoomService.deleteChatRoom(testUser1.getUsername(), roomId);

        // when
        Long result = chatRoomService.createChatRoom("닉네임2", testUser1.getUsername());

        // then
        ChatRoom chatRoom = chatRoomRepository.findBySenderAndReceiver(testUser1, testUser2);
        Assertions.assertThat(result).isEqualTo(chatRoom.getId());
    }

    @Test
    void 채팅방생성_이미생성된방이있을때_receiver기준() {
        // given
        Long roomId = chatRoomService.createChatRoom("닉네임2", testUser1.getUsername());
        chatRoomService.deleteChatRoom(testUser2.getUsername(), roomId);

        // when
        Long result = chatRoomService.createChatRoom("닉네임1", testUser2.getUsername());

        // then
        ChatRoom chatRoom = chatRoomRepository.findBySenderAndReceiver(testUser1, testUser2);
        Assertions.assertThat(result).isEqualTo(chatRoom.getId());
    }

    @Test
    void 내채팅방목록조회() {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        List<ChatRoomResponse> result = chatRoomService.findChatRooms(testUser1.getUsername());

        // then
        ChatRoom chatRoom1 = chatRoomRepository.findBySenderAndReceiver(testUser1, testUser2);
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getId()).isEqualTo(chatRoom1.getId());

    }

    @Test
    void 채팅방삭제_sender기준() {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        chatRoomService.deleteChatRoom(testUser1.getUsername(), chatRoom.getId());

        // then
        Assertions.assertThat(chatRoom.getIsSenderDeleted()).isTrue();
        Assertions.assertThat(chatRoom.getIsReceiverDeleted()).isFalse();
    }

    @Test
    void 채팅방삭제_receiver기준() {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        chatRoomService.deleteChatRoom(testUser2.getUsername(), chatRoom.getId());

        // then
        Assertions.assertThat(chatRoom.getIsSenderDeleted()).isFalse();
        Assertions.assertThat(chatRoom.getIsReceiverDeleted()).isTrue();
    }
}