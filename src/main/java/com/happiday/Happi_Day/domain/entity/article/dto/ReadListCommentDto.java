package com.happiday.Happi_Day.domain.entity.article.dto;

import com.happiday.Happi_Day.domain.entity.article.ArticleComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReadListCommentDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private Long articleId;

    public static ReadListCommentDto fromEntity(ArticleComment comment) {
        return ReadListCommentDto.builder()
                .id(comment.getId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .articleId(comment.getArticle().getId())
                .build();
    }
}
