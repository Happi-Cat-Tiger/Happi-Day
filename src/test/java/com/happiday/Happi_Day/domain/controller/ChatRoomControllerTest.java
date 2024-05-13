package com.happiday.Happi_Day.domain.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.chat.dto.ChatNicknameDto;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ChatMessageRepository;
import com.happiday.Happi_Day.domain.repository.ChatRoomRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.assertj.core.api.Assertions;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ChatRoomController chatRoomController;

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
    void 채팅방생성_성공() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser1.getUsername());

        ChatNicknameDto dto = new ChatNicknameDto("닉네임2");
        String body = objectMapper.writeValueAsString(dto);

        // when // then
        mockMvc.perform(post("/api/v1/chat")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 채팅방생성_실패_닉네임존재x() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser1.getUsername());

        ChatNicknameDto dto = new ChatNicknameDto("가나다");
        String body = objectMapper.writeValueAsString(dto);

        // when // then
        mockMvc.perform(post("/api/v1/chat")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 채팅방생성_실패_내닉네임작성() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser1.getUsername());

        ChatNicknameDto dto = new ChatNicknameDto("닉네임1");
        String body = objectMapper.writeValueAsString(dto);

        // when // then
        mockMvc.perform(post("/api/v1/chat")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 채팅방목록가져오기() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser1.getUsername());

        // when // then
        mockMvc.perform(get("/api/v1/chat/rooms"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 채팅방열기_채팅내용목록가져오기() throws Exception {
        // given
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

        // when // then
        mockMvc.perform(get("/api/v1/chat/{roomId}", chatRoom.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 채팅방삭제() throws Exception {
        // given
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

        // when
        mockMvc.perform(delete("/api/v1/chat/{roomId}", chatRoom.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        Assertions.assertThat(chatRoom.getIsSenderDeleted()).isTrue();
    }

    @Test
    void 채팅읽기() throws Exception {
        // given
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
                .sender(testUser2)
                .content("안녕하세요")
                .checked(false)
                .build();
        chatMessageRepository.save(chatMessage);

        // when
        mockMvc.perform(put("/api/v1/chat/rooms/{roomId}/read", chatRoom.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        Assertions.assertThat(chatMessage.getChecked()).isTrue();
    }
}