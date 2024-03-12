package com.happiday.Happi_Day.domain.repository.article;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArticleLikeRepository;
import com.happiday.Happi_Day.domain.repository.ArticleRepository;
import com.happiday.Happi_Day.domain.repository.BoardCategoryRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    BoardCategoryRepository boardCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleLikeRepository articleLikeRepository;

    @Autowired
    private EntityManager entityManager;

    private Article article;

    @BeforeEach
    void beforeEach() {
        // 카테고리 생성
        BoardCategory testCategory = BoardCategory.builder().name("test Category").description("test Category description").build();
        boardCategoryRepository.save(testCategory);

        // 유저 생성
        User testUser = User.builder()
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

        // 이미지 url 리스트
        List<String> imageList = new ArrayList<>();

        imageList.add("http://example.com/article_image1.jpg");
        imageList.add("http://example.com/article_image2.jpg");
        imageList.add("http://example.com/article_image3.jpg");

        article = Article.builder()
                .user(testUser)
                .category(testCategory)
                .title("test title")
                .content("test content")
                .thumbnailUrl("http://example.com/article_thumnail.jpg")
                .imageUrl(imageList)
                .eventAddress("test eventAddress")
                .build();
        article = articleRepository.save(article);
    }


    @Test
    @DisplayName("카테고리 게시글 조회")
    public void findArticleByCategoryTest() {
        // given
        String testTitle = "test title";
        Optional<BoardCategory> testCategory = boardCategoryRepository.findById(article.getCategory().getId());
        Pageable pageable = PageRequest.of(0, 12);

        // when
        Page<Article> foundArticle = articleRepository.findAllByCategory(testCategory.get(), pageable);

        // then
        assertThat(foundArticle.getContent().get(0).getTitle()).isEqualTo(testTitle);
    }

    @Test
    @DisplayName("유저로 글 조회")
    public void findArticleByUserTest() {
        // given
        String testTitle = "test title";
        Optional<User> testUser = userRepository.findById(article.getUser().getId());
        Pageable pageable = PageRequest.of(0, 12);

        // when
        Page<Article> foundArticle = articleRepository.findAllByUser(testUser.get(), pageable);

        // then
        assertThat(foundArticle.getContent().get(0).getTitle()).isEqualTo(testTitle);
    }

    @Test
    @DisplayName("글 제목으로 존재 여부 확인")
    public void existsArticleByTitleTest() {
        // given
        String testTitle = "test title";

        // when
        boolean exists = articleRepository.existsByTitle(testTitle);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @Transactional
    @DisplayName("조회수 증가 확인")
    public void increaseViewCountTest() {
        // given
        int beforeIncrease = article.getViewCount();

        // when
        articleRepository.increaseViewCount(article.getId());
        entityManager.flush();
        entityManager.clear();

        // then
        Article updateArticle = articleRepository.findById(article.getId()).get();
        assertThat(updateArticle.getViewCount()).isEqualTo(beforeIncrease + 1);

    }

}
