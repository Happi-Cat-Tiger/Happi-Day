package com.happiday.Happi_Day.domain.entity.article.dto;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleHashtag;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@Slf4j
public class ReadOneArticleDto {
    private Long id;
    private Long categoryId;
    private String title;
    private String thumbnailImage;
    private String content;
    private String eventAddress;
    private String eventDetailAddress;
    private List<String> artists;
    private List<String> teams;
    private List<String> hashtags;
    private String user;
    private String updatedAt;
    private List<ReadCommentDto> comments;
    private int likeUsersNum;
    private List<String> imageUrl;
    private int viewCount;

    public static ReadOneArticleDto fromEntity(Article article) {
        return ReadOneArticleDto.builder()
                .id(article.getId())
                .categoryId(article.getCategory().getId())
                .user(article.getUser().getNickname())
                .title(article.getTitle())
                .thumbnailImage(article.getThumbnailUrl())
                .content(article.getContent())
                .eventAddress(article.getEventAddress())
                .eventDetailAddress(article.getEventDetailAddress())
                .comments(ReadCommentDto.toReadCommentDto(article.getArticleComments()))
                .artists(article.getArtistArticleList().stream().map(ArtistArticle::getArtist).map(Artist::getName).collect(Collectors.toList()))
                .teams(article.getTeamArticleList().stream().map(TeamArticle::getTeam).map(Team::getName).collect(Collectors.toList()))
                .hashtags(article.getArticleHashtags().stream().map(ArticleHashtag::getHashtag).map(Hashtag::getTag).collect(Collectors.toList()))
                .likeUsersNum(article.getArticleLikes().size())
                .imageUrl(article.getImageUrl())
                .updatedAt(article.getUpdatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .viewCount(article.getViewCount())
                .build();
    }
}
