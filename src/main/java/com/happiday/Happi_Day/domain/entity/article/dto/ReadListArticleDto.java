package com.happiday.Happi_Day.domain.entity.article.dto;

import com.happiday.Happi_Day.domain.entity.article.Article;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ReadListArticleDto {
    private Long id;
    private String category;
    private String title;
    private String nickname;
    private String date;
    private String thumbnailUrl;
    private int commentNum;
    private int viewCount;

    public static ReadListArticleDto fromEntity(Article article) {
        return ReadListArticleDto.builder()
                .id(article.getId())
                .category(article.getCategory().getName())
                .nickname(article.getUser().getNickname())
                .title(article.getTitle())
                .date(article.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .thumbnailUrl(article.getThumbnailUrl())
                .commentNum(article.getArticleComments().size())
                .viewCount(article.getViewCount())
                .build();
    }
}
