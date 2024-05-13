package com.happiday.Happi_Day.domain.repository.chat;

import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
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
@Transactional
@ActiveProfiles("test")
class ChatMessageRepositoryTest {

    @Autowired
    ChatMessageRepository chatMessageRepository;

    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Autowired
    UserRepository userRepository;

    private User user1;

    private User user2;

    private ChatRoom chatRoom;

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

        chatRoom = ChatRoom.builder()
                .sender(user1)
                .receiver(user2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);
    }

    @Test
    void Page_findAllByChatRoom_IdOrderByIdDesc() {
        // given
        Pageable pageable = PageRequest.of(0, 20);

        ChatMessage chatMessage1 = ChatMessage.builder()
                .content("안녕하세요")
                .sender(user1)
                .checked(false)
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage1);

        ChatMessage chatMessage2 = ChatMessage.builder()
                .content("안녕하세요2")
                .sender(user2)
                .checked(false)
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage2);

        // when
        Page<ChatMessage> result = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(chatRoom.getId(), pageable);

        // then
        Assertions.assertThat(result.getNumberOfElements()).isEqualTo(2);
        Assertions.assertThat(result.getContent().get(0).getSender().getNickname()).isEqualTo(user2.getNickname());
    }

    @Test
    void List_findAllByChatRoom_IdOrderByIdDesc() {
        // given
        ChatMessage chatMessage1 = ChatMessage.builder()
                .content("안녕하세요")
                .sender(user1)
                .checked(false)
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage1);

        ChatMessage chatMessage2 = ChatMessage.builder()
                .content("안녕하세요2")
                .sender(user2)
                .checked(false)
                .chatRoom(chatRoom)
                .build();
        chatMessageRepository.save(chatMessage2);

        // when
        List<ChatMessage> result = chatMessageRepository.findAllByChatRoom_IdOrderByIdDesc(chatRoom.getId());

        // then
        Assertions.assertThat(result.size()).isEqualTo(2);
        Assertions.assertThat(result.get(1).getSender().getNickname()).isEqualTo(user1.getNickname());
    }
}