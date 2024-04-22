package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
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
@Transactional
@ActiveProfiles("test")
class ChatRoomRepositoryTest {

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    UserRepository userRepository;

    private User user1;

    private User user2;

    private User user3;

    @BeforeEach
    public void init() {
        user1 = User.builder()
                .username("user1@email.com")
                .password("qwer1234")
                .nickname("유저1")
                .realname("김유저")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(user1);

        user2 = User.builder()
                .username("user2@email.com")
                .password("qwer1234")
                .nickname("유저2")
                .realname("이유저")
                .phone("01012341234")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(user2);

        user3 = User.builder()
                .username("user3@email.com")
                .password("qwer1234")
                .nickname("유저3")
                .realname("박유저")
                .phone("01012344321")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        userRepository.save(user3);
    }

    @Test
    void saveChatRoom() {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(user1)
                .receiver(user2)
                .chatMessages(new ArrayList<>())
                .build();

        // when
        chatRoomRepository.save(chatRoom);

        // then
        ChatRoom result = chatRoomRepository.findBySender(user1);
        System.out.println(result.getIsSenderDeleted());
        Assertions.assertThat(result.getId()).isNotNull();
    }


    @Test
    void findBySenderAndReceiver() {
        // given
        ChatRoom chatRoom = ChatRoom.builder()
                .sender(user1)
                .receiver(user2)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);

        // when
        ChatRoom result = chatRoomRepository.findBySenderAndReceiver(user1, user2);

        // then
        Assertions.assertThat(result.getSender()).isEqualTo(user1);
        Assertions.assertThat(result.getReceiver()).isEqualTo(user2);
    }

    @Test
    void findAllBySenderAndIsSenderDeletedFalse() {
        // given
        ChatRoom chatRoom1 = ChatRoom.builder()
                .sender(user1)
                .receiver(user3)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom1);

        ChatRoom chatRoom2 = ChatRoom.builder()
                .sender(user1)
                .receiver(user2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom2);

        // when
        List<ChatRoom> result = chatRoomRepository.findAllBySenderAndIsSenderDeletedFalse(user1);
        // then
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    void findAllByReceiverAndIsReceiverDeletedFalse() {
        // given
        ChatRoom chatRoom1 = ChatRoom.builder()
                .sender(user1)
                .receiver(user3)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom1);

        ChatRoom chatRoom2 = ChatRoom.builder()
                .sender(user2)
                .receiver(user3)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom2);

        // when
        List<ChatRoom> result = chatRoomRepository.findAllByReceiverAndIsReceiverDeletedFalse(user3);

        // then
        Assertions.assertThat(result.size()).isEqualTo(2);
    }
}