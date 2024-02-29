package com.happiday.Happi_Day.domain.entity.article.dto;

import com.happiday.Happi_Day.domain.entity.article.Article;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
public class WriteArticleDto {
    @NotBlank(message="제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    private String eventAddress;
    private String eventDetailAddress;

    private List<String> hashtag;

    public Article toEntity(){
        return Article.builder()
                .title(title)
                .content(content)
                .eventAddress(eventAddress)
                .eventDetailAddress(eventDetailAddress)
                .build();
    }

}
