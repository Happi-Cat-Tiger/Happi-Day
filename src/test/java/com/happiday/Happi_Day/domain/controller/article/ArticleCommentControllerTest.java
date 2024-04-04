package com.happiday.Happi_Day.domain.controller.article;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleComment;
import com.happiday.Happi_Day.domain.entity.article.dto.WriteCommentDto;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArticleCommentRepository;
import com.happiday.Happi_Day.domain.repository.ArticleRepository;
import com.happiday.Happi_Day.domain.repository.BoardCategoryRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.utils.SecurityUtils;
import org.junit.jupiter.api.*;
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
public class ArticleCommentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    BoardCategoryRepository boardCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ArticleCommentRepository articleCommentRepository;

    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
    private BoardCategory testCategory;
    private User testUser;
    private Article testArticle;
    private ArticleComment testComment;

    @BeforeAll
    public static void beforeAll(){
        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
    }

    @AfterAll
    public static void afterAll(){
        securityUtilsMockedStatic.close();
    }

    @BeforeEach
    void beforeEach(){
        // 카테고리 생성
        testCategory = BoardCategory.builder().name("test Category").description("test Category description").build();
        boardCategoryRepository.save(testCategory);

        // 유저 생성
        testUser = User.builder()
                .username("test email")
                .password("test password")
                .nickname("test nickname")
                .realname("test name")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        testUser = userRepository.save(testUser);

        // 글 생성
        testArticle = Article.builder()
                .user(testUser)
                .category(testCategory)
                .title("Article title")
                .content("Article content")
                .articleComments(new ArrayList<>())
                .artistArticleList(new ArrayList<>())
                .teamArticleList(new ArrayList<>())
                .articleHashtags(new ArrayList<>())
                .articleLikes(new ArrayList<>())
                .build();
        articleRepository.save(testArticle);

        testComment = ArticleComment.builder()
                .article(testArticle)
                .user(testUser)
                .content("comment")
                .build();
        articleCommentRepository.save(testComment);
    }

    @Test
    @DisplayName("댓글 작성")
    public void writeCommentTest() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        WriteCommentDto dto = new WriteCommentDto();
        dto.setContent("test ArticleComment");
        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/articles/"+testArticle.getId()+"/comments")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("댓글 조회")
    public void readCommentTest() throws Exception {
        mockMvc.perform(get("/api/v1/articles/"+testArticle.getId()+"/comments"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 수정")
    public void updateCommentTest() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        WriteCommentDto dto = new WriteCommentDto();
        dto.setContent("update ArticleComment");
        String body = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/v1/articles/"+testArticle.getId()+"/comments/"+testComment.getId())
                .content(body)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("댓글 삭제")
    public void deleteCommentTest() throws Exception {
        // given
        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());

        mockMvc.perform(delete("/api/v1/articles/"+testArticle.getId()+"/comments/"+testComment.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
