package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleInitService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final BoardCategoryRepository boardCategoryRepository;
    private final ArtistRepository artistRepository;
    private final ArtistArticleRepository artistArticleRepository;
    private final TeamArticleRepository teamArticleRepository;
    private final TeamRepository teamRepository;
    private final DefaultImageUtils defaultImageUtils;

    public void initArticles() {
        User writer = userRepository.findById(2L).orElse(null);
        BoardCategory category = boardCategoryRepository.findById(1L).orElse(null);
//        Artist artist1 = artistRepository.findById(1L).orElse(null);
//        Artist artist2 = artistRepository.findById(2L).orElse(null);
//        Artist artist3 = artistRepository.findById(3L).orElse(null);
//        Artist artist4 = artistRepository.findById(4L).orElse(null);
//        Team team1 = teamRepository.findById(1L).orElse(null);
//        Team team2 = teamRepository.findById(2L).orElse(null);
        String imageUrl = defaultImageUtils.getDefaultImageUrlArticleThumbnail();

        List<Long> artistForArticle1 = List.of(1L, 2L);
        List<Long> artistForArticle2 = List.of(3L, 4L);
        List<Long> teamForArticle1 = List.of(1L);
        List<Long> teamForArticle2 = List.of(2L);

        Article article1 = createArticle(writer, "동방신기 제목", "동방신기 내용", category, imageUrl);
        Article article2 = createArticle(writer, "god 제목", "god 내용", category, imageUrl);

        List<Article> articles = List.of(article1, article2);

        articles.forEach(article -> {
            try {
                if (!articleRepository.existsByTitle(article.getTitle())) {
                    articleRepository.save(article);
                    if (article == article1) {
                        linkArtistsToArticle(article, artistForArticle1);
                        linkTeamToArticle(article, teamForArticle1);
                    } else {
                        linkArtistsToArticle(article, artistForArticle2);
                        linkTeamToArticle(article, teamForArticle2);
                    }
                }
            } catch (Exception e) {
                log.error("DB Seeder 게시글 저장 중 예외 발생 - 제목: {}", article.getTitle(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_ARTICLE_SAVE_ERROR);
            }
        });
    }

    private Article createArticle(User writer, String title, String content, BoardCategory category, String imageUrl) {
        return Article.builder()
                .user(writer)
                .title(title)
                .content(content)
                .category(category)
                .thumbnailUrl(imageUrl)
                .build();
    }

    private void linkArtistsToArticle(Article article, List<Long> artistIds) {
        artistIds.forEach(artistId -> {
            artistRepository.findById(artistId).ifPresent(artist -> {
                ArtistArticle artistArticle = ArtistArticle.builder()
                        .article(article)
                        .artist(artist)
                        .build();
                artistArticleRepository.save(artistArticle);
            });
        });
    }

    private void linkTeamToArticle(Article article, List<Long> teamIds) {
        teamIds.forEach(teamId -> {
            teamRepository.findById(teamId).ifPresent(team -> {
                TeamArticle teamArticle = TeamArticle.builder()
                        .article(article)
                        .team(team)
                        .build();
                teamArticleRepository.save(teamArticle);
            });
        });
    }
}
