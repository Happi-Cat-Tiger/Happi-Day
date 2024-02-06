package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamArticleRepository extends JpaRepository<TeamArticle, Long> {
    void deleteByArticleAndTeam(Article article, Team team);
    void deleteByArticle(Article article);
}
