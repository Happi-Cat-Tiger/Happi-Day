package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistArticleRepository extends JpaRepository<ArtistArticle, Long> {
    void deleteByArticleAndArtist(Article article, Artist artist);
    void deleteByArticle(Article article);
}
