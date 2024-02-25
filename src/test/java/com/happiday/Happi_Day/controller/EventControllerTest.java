package com.happiday.Happi_Day.controller;

import com.happiday.Happi_Day.domain.controller.EventController;
import com.happiday.Happi_Day.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    // mockMvc 초기화, 각메서드가 실행되기전에 초기화 되게 함
    // standaloneMockMvcBuilder() 호출하고 스프링 테스트의 설정을 커스텀하여 mockMvc 객체 생성
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }


}
