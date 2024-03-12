package com.happiday.Happi_Day.domain.repository.article;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleLike;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArticleLikeRepository;
import com.happiday.Happi_Day.domain.repository.ArticleRepository;
import com.happiday.Happi_Day.domain.repository.BoardCategoryRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArticleLikeRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    BoardCategoryRepository boardCategoryRepository;

    @Autowired
    ArticleLikeRepository articleLikeRepository;

    @Autowired
    UserRepository userRepository;

    private Article article;
    private User user;

    @BeforeEach
    void beforeEach() {
        // 카테고리 생성
        BoardCategory testCategory = BoardCategory.builder().name("test Category").description("test Category description").build();
        boardCategoryRepository.save(testCategory);

        // 유저 생성
        user = User.builder()
                .username("test email")
                .password("test password")
                .nickname("test nickname")
                .realname("test name")
                .phone("01012345678")
                .role(RoleType.USER)
                .isActive(true)
                .isTermsAgreed(true)
                .build();
        user = userRepository.save(user);

        // 이미지 url 리스트
        List<String> imageList = new ArrayList<>();

        imageList.add("http://example.com/article_image1.jpg");
        imageList.add("http://example.com/article_image2.jpg");
        imageList.add("http://example.com/article_image3.jpg");

        article = Article.builder()
                .user(user)
                .category(testCategory)
                .title("test title")
                .content("test content")
                .thumbnailUrl("http://example.com/article_thumnail.jpg")
                .imageUrl(imageList)
                .eventAddress("test eventAddress")
                .build();
        article = articleRepository.save(article);

        ArticleLike newArticleLike = ArticleLike.builder()
                .article(article)
                .user(user)
                .build();
        articleLikeRepository.save(newArticleLike);
    }

    @Test
    @DisplayName("유저가 게시글에 좋아요를 했는지 확인")
    public void findLikeByUserAndArticleTest() {
        // when
        Optional<ArticleLike> foundLike = articleLikeRepository.findByUserAndArticle(user, article);

        // then
        assertThat(foundLike).isPresent();
        assertThat(foundLike.get().getUser()).isEqualTo(user);
        assertThat(foundLike.get().getArticle()).isEqualTo(article);
    }

    @Test
    @DisplayName("게시글에 좋아요를 누른 유저 조회")
    public void findLikeByArticleTest() {
        // when
        List<ArticleLike> likes = articleLikeRepository.findByArticle(article);

        // then
        assertThat(likes.size()).isEqualTo(1);
        assertThat(likes.get(0).getArticle()).isEqualTo(article);
    }

}
