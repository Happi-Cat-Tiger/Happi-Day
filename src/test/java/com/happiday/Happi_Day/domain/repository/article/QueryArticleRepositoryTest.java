package com.happiday.Happi_Day.domain.repository.article;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.user.RoleType;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QueryArticleRepositoryTest {
    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BoardCategoryRepository boardCategoryRepository;

    @Autowired
    QueryArticleRepository queryArticleRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    ArtistArticleRepository artistArticleRepository;

    @Autowired
    ArtistSubscriptionRepository artistSubscriptionRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    TeamArticleRepository teamArticleRepository;

    @Autowired
    TeamSubscriptionRepository teamSubscriptionRepository;

    private Article article;
    private User user;
    private Artist artist;
    private Team team;
    private ArtistArticle artistArticle;
    private TeamArticle teamArticle;

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

        // 글 생성
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

        // 아티스트 생성
        artist = artistRepository.save(Artist.builder()
                .name("Test Artist")
                .description("Test Artist Description")
                .profileUrl("http://example.com/profile.jpg")
                .build());

        // 아티스트-게시글 연관관계 설정
        artistArticle = ArtistArticle.builder()
                .article(article)
                .artist(artist)
                .build();
        artistArticleRepository.save(artistArticle);

        // 아티스트-유저 연관관계 설정
        ArtistSubscription artistSubscription = ArtistSubscription.builder()
                .user(user)
                .artist(artist)
                .subscribedAt(LocalDateTime.now())
                .build();
        artistSubscriptionRepository.save(artistSubscription);
        List<ArtistSubscription> artistSubscriptionList = new ArrayList<>();
        artistSubscriptionList.add(artistSubscription);

        // 팀 생성
        team = teamRepository.save(Team.builder()
                .name("Test Team")
                .description("Test Team Description")
                .build());

        // 팀-게시글 연관관계 설정
        teamArticle = TeamArticle.builder()
                .article(article)
                .team(team)
                .build();
        teamArticleRepository.save(teamArticle);

        // 팀-유저 연관관계 설정
        TeamSubscription teamSubscription = TeamSubscription.builder()
                .user(user)
                .team(team)
                .subscribedAt(LocalDateTime.now())
                .build();
        teamSubscriptionRepository.save(teamSubscription);
        List<TeamSubscription> teamSubscriptionList = new ArrayList<>();
        teamSubscriptionList.add(teamSubscription);


        User updatedUser = user.toBuilder()
                .artistSubscriptionList(artistSubscriptionList)
                .teamSubscriptionList(teamSubscriptionList)
                .build();
        userRepository.save(updatedUser);
    }

    @Test
    @DisplayName("필터링 게시글 조회")
    public void findArticleByFilterAndKeywordTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test title";
        Long categoryId = article.getCategory().getId();

        // when
        Page<Article> foundArticle = queryArticleRepository.findArticleByFilterAndKeyword(pageable, categoryId, filter, keyword);

        // then
        assertThat(foundArticle).isNotNull();
        assertThat(foundArticle.getContent()).isNotEmpty();
        assertThat(foundArticle.getContent().get(0).getTitle()).isEqualTo("test title");
    }

    @Test
    @DisplayName("구독한 아티스트의 글 중 필터링 게시글 조회")
    public void findArticleByFilterAndKeywordAndSubscribedArtistTest() {
        // given
        Pageable pageable = PageRequest.of(0, 12);
        String filter = "name";
        String keyword = "test";
        Long categoryId = article.getCategory().getId();

        // when
        Page<Article> foundArticle = queryArticleRepository.findArticleByFilterAndKeywordAndSubscribedArtists(pageable, categoryId, filter, keyword, user);

        // then
        assertThat(foundArticle).isNotNull();
        assertThat(foundArticle.getContent()).isNotEmpty();
    }
}
