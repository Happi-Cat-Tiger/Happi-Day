//package com.happiday.Happi_Day.domain.controller.article;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.happiday.Happi_Day.domain.controller.ArticleController;
//import com.happiday.Happi_Day.domain.entity.article.Article;
//import com.happiday.Happi_Day.domain.entity.article.dto.WriteArticleDto;
//import com.happiday.Happi_Day.domain.entity.artist.Artist;
//import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
//import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
//import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
//import com.happiday.Happi_Day.domain.entity.user.RoleType;
//import com.happiday.Happi_Day.domain.entity.user.User;
//import com.happiday.Happi_Day.domain.repository.*;
//import com.happiday.Happi_Day.utils.SecurityUtils;
//import org.junit.jupiter.api.*;
//import org.mockito.MockedStatic;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//public class ArticleControllerTest {
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    BoardCategoryRepository boardCategoryRepository;
//
//    @Autowired
//    UserRepository userRepository;
//
//    @Autowired
//    ArticleController articleController;
//
//    @Autowired
//    ArticleRepository articleRepository;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    ArtistRepository artistRepository;
//
//    @Autowired
//    ArtistSubscriptionRepository artistSubscriptionRepository;
//
//    @Autowired
//    ArtistArticleRepository artistArticleRepository;
//
//    private BoardCategory testCategory;
//    private MultipartFile thumbnailImage;
//    private List<MultipartFile> imageList;
//    private static MockedStatic<SecurityUtils> securityUtilsMockedStatic;
//    private User testUser;
//    private Article testArticle;
//    private Artist artist;
//
//    @BeforeAll
//    public static void beforeAll() {
//        securityUtilsMockedStatic = mockStatic(SecurityUtils.class);
//    }
//
//    @AfterAll
//    public static void afterAll() {
//        securityUtilsMockedStatic.close();
//    }
//
//    @BeforeEach
//    void beforeEach() {
//        // 카테고리 생성
//        testCategory = BoardCategory.builder().name("test Category").description("test Category description").build();
//        boardCategoryRepository.save(testCategory);
//
//        // 아티스트 생성
//        List<Artist> artists = new ArrayList<>();
//        artist = Artist.builder()
//                .name("artist")
//                .description("artist description")
//                .profileUrl("imageUrl")
//                .build();
//        artistRepository.save(artist);
//        artists.add(artist);
//
//        // 구독한 아티스트
//        List<ArtistSubscription> subList = new ArrayList<>();
//        ArtistSubscription sub = ArtistSubscription.builder()
//                .user(testUser)
//                .artist(artist)
//                .build();
//        artistSubscriptionRepository.save(sub);
//        subList.add(sub);
//
//        // 유저 생성
//        testUser = User.builder()
//                .username("test email")
//                .password("test password")
//                .nickname("test nickname")
//                .realname("test name")
//                .phone("01012345678")
//                .role(RoleType.USER)
//                .isActive(true)
//                .isTermsAgreed(true)
//                .artistSubscriptionList(subList)
//                .teamSubscriptionList(new ArrayList<>())
//                .build();
//        testUser = userRepository.save(testUser);
//
//        // 썸네일 이미지 생성
//        thumbnailImage = new MockMultipartFile("thumbnailImage", "thumbnailImage.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
//
//        // 이미지 생성
//        imageList = new ArrayList<>();
//        MultipartFile image1 = new MockMultipartFile("image1", "image1.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
//        MultipartFile image2 = new MockMultipartFile("image2", "image2.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
//        MultipartFile image3 = new MockMultipartFile("image3", "image3.jpg", MediaType.IMAGE_JPEG_VALUE, "ImageData".getBytes());
//        imageList.add(image1);
//        imageList.add(image2);
//        imageList.add(image3);
//
//        // 글 생성
//        testArticle = Article.builder()
//                .user(testUser)
//                .category(testCategory)
//                .title("Article title")
//                .content("Article content")
//                .thumbnailUrl(thumbnailImage.toString())
//                .imageUrl(imageList.stream().map(image -> image.toString()).collect(Collectors.toList()))
//                .articleComments(new ArrayList<>())
//                .artistArticleList(new ArrayList<>())
//                .teamArticleList(new ArrayList<>())
//                .articleHashtags(new ArrayList<>())
//                .articleLikes(new ArrayList<>())
//                .build();
//        articleRepository.save(testArticle);
//
//    }
//
//    @Test
//    @DisplayName("글 작성")
//    public void wrtieArticleTest() throws Exception {
//        // given
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        List<String> hashtags = new ArrayList<>();
//        hashtags.add("update hashtag1");
//        hashtags.add("update hashtag2");
//
//        WriteArticleDto dto = WriteArticleDto.builder()
//                .title("test Article title")
//                .content("test Article content")
//                .hashtag(hashtags)
//                .build();
//        String body = objectMapper.writeValueAsString(dto);
//
//        MockMultipartFile articlePart = new MockMultipartFile("article", "", MediaType.APPLICATION_JSON_VALUE, body.getBytes());
//
//        // when
//        mockMvc.perform(
//                        multipart("/api/v1/articles/" + testCategory.getId())
//                                .file(articlePart)
//                                .contentType(MediaType.MULTIPART_FORM_DATA)
//                )
//                .andDo(print())
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @DisplayName("글 상세 조회")
//    public void readOneTest() throws Exception {
//        mockMvc.perform(get("/api/v1/articles/" + testArticle.getId()))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("글 목록 조회")
//    public void readListTest() throws Exception {
//        mockMvc.perform(get("/api/v1/articles/" + testCategory.getId() + "/list"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("구독중인 아티스트/팀 게시글 조회")
//    public void readArticleBySubscribedArtists() throws Exception {
//        // given
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        testArticle = Article.builder()
//                .user(testUser)
//                .category(testCategory)
//                .title("Article title")
//                .content("Article content")
//                .thumbnailUrl(thumbnailImage.toString())
//                .imageUrl(imageList.stream().map(image -> image.toString()).collect(Collectors.toList()))
//                .articleComments(new ArrayList<>())
//                .artistArticleList(new ArrayList<>())
//                .teamArticleList(new ArrayList<>())
//                .articleHashtags(new ArrayList<>())
//                .articleLikes(new ArrayList<>())
//                .build();
//        articleRepository.save(testArticle);
//
//        List<ArtistArticle> artistArticleList = new ArrayList<>();
//        ArtistArticle artistArticle = ArtistArticle.builder()
//                .article(testArticle)
//                .artist(artist)
//                .build();
//        artistArticleRepository.save(artistArticle);
//        artistArticleList.add(artistArticle);
//
//        // when
//        mockMvc.perform(get("/api/v1/articles/" + testCategory.getId() + "/list/subscribedArtists"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("글 전체글 조회")
//    public void readAllArticlesTest() throws Exception {
//        // when
//        mockMvc.perform(get("/api/v1/articles"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("글 수정")
//    public void updateArticle() throws Exception {
//        // given
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        List<String> hashtags = new ArrayList<>();
//        hashtags.add("update hashtag1");
//        hashtags.add("update hashtag2");
//
//        WriteArticleDto updateDto = WriteArticleDto.builder()
//                .title("test Article title")
//                .content("test Article content")
//                .hashtag(hashtags)
//                .build();
//        String body = objectMapper.writeValueAsString(updateDto);
//
//        MockMultipartFile updateArticlePart = new MockMultipartFile("article", "", MediaType.APPLICATION_JSON_VALUE, body.getBytes());
//
//        mockMvc.perform(
//                multipart("/api/v1/articles/" + testArticle.getId())
//                        .file(updateArticlePart)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .with(request -> {
//                            request.setMethod("PUT");
//                            return request;
//                        })
//        ).andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("글 삭제")
//    public void deleteArticleTest() throws Exception {
//        // given
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        mockMvc.perform(delete("/api/v1/articles/" + testArticle.getId()))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @DisplayName("글 좋아요")
//    public void likeArticleTest() throws Exception {
//        // given
//        when(SecurityUtils.getCurrentUsername()).thenReturn(testUser.getUsername());
//
//        mockMvc.perform(post("/api/v1/articles/" + testArticle.getId() + "/like"))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//}
