package com.happiday.Happi_Day.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatMessageDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatController chatController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;

    private User testUser1;

    private User testUser2;

    private ChatRoom chatRoom;

    @BeforeAll
    public static void beforeAll() {
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        securityUtilsMockedStatic.close();
    }

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
    void 채팅보내기() throws Exception {

        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser1.getUsername());

        ChatRoom chatRoom = ChatRoom.builder()
                .sender(testUser1)
                .receiver(testUser2)
                .isSenderDeleted(false)
                .isReceiverDeleted(false)
                .open(false)
                .chatMessages(new ArrayList<>())
                .build();
        chatRoomRepository.save(chatRoom);

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(testUser1)
                .content("안녕하세요")
                .checked(false)
                .build();
        chatMessageRepository.save(chatMessage);

        ChatMessageDto dto = ChatMessageDto.fromEntity(chatMessage);

        String body = objectMapper.writeValueAsString(dto);

        // when
        mockMvc.perform(post("/api/v1/chat/{roomId}/send", chatRoom.getId())
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }
}