package com.happiday.Happi_Day.domain.repository.article;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleHashtag;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ArticleHashtagRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleHashtagRepository articleHashtagRepository;

    @Autowired
    BoardCategoryRepository boardCategoryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    HashtagRepository hashtagRepository;

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

        // 해시태그 생성
        Hashtag hashtag1 = Hashtag.builder()
                .tag("hashtag1")
                .build();
        hashtagRepository.save(hashtag1);

        Hashtag hashtag2 = Hashtag.builder()
                .tag("hashtag2")
                .build();
        hashtagRepository.save(hashtag2);
    }

    @Test
    @DisplayName("게시글로 삭제")
    public void deleteHashtagByArticleTest() {
        // given
        ArticleHashtag articleHashtag1 = ArticleHashtag.builder()
                .hashtag(hashtagRepository.findByTag("hashtag1").get())
                .article(article)
                .build();
        ArticleHashtag articleHashtag2 = ArticleHashtag.builder()
                .hashtag(hashtagRepository.findByTag("hashtag2").get())
                .article(article)
                .build();
        articleHashtagRepository.save(articleHashtag1);
        articleHashtagRepository.save(articleHashtag2);

        // when
        articleHashtagRepository.deleteByArticle(article);

        // then
        boolean exists1 = articleHashtagRepository.existsById(articleHashtag1.getId());
        boolean exists2 = articleHashtagRepository.existsById(articleHashtag2.getId());
        assertThat(exists1).isFalse();
        assertThat(exists2).isFalse();
    }

}
