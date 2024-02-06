package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.article.ArticleHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleHashtagRepository extends JpaRepository<ArticleHashtag, Long> {
}
