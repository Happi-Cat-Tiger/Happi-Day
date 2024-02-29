package com.happiday.Happi_Day.domain.entity.event.dto;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class EventListResponseDto {
    private Long id;

    private String nickname;

    private String title;

    private LocalDateTime updatedAt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
    
    private String location;

    private String thumbnailUrl;

    private List<String> artists;

    private List<String> teams;

    private List<String> hashtags;

    private int commentCount;

    private int likeCount;

    private int viewCount;

    private int reviewCount;

    public EventListResponseDto() {
    }

    public EventListResponseDto(Long id, String nickname, String title, LocalDateTime updatedAt, LocalDateTime startTime, LocalDateTime endTime, String location, String thumbnailUrl, List<String> artists, List<String> teams, List<String> hashtags, int commentCount, int likeCount, int viewCount, int reviewCount) {
        this.id = id;
        this.nickname = nickname;
        this.title = title;
        this.updatedAt = updatedAt;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.thumbnailUrl = thumbnailUrl;
        this.artists = artists;
        this.teams = teams;
        this.hashtags = hashtags;
        this.commentCount = commentCount;
        this.likeCount = likeCount;
        this.viewCount = viewCount;
        this.reviewCount = reviewCount;

    }

    public static EventListResponseDto fromEntity(Event event) {

        return EventListResponseDto.builder()
                .id(event.getId())
                .nickname(event.getUser().getNickname())
                .title(event.getTitle())
                .updatedAt(event.getUpdatedAt())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .thumbnailUrl(event.getThumbnailUrl())
                .artists(event.getArtistsEventList().stream().map(ArtistEvent::getArtist).map(Artist::getName).collect(Collectors.toList()))
                .teams(event.getTeamsEventList().stream().map(TeamEvent::getTeam).map(Team::getName).collect(Collectors.toList()))
                .hashtags(event.getEventHashtags().stream().map(EventHashtag::getHashtag).map(Hashtag::getTag).collect(Collectors.toList()))
                .commentCount(event.getComments().size())
                .likeCount(event.getLikes().size())
                .viewCount(event.getViewCount())
                .reviewCount(event.getReviewCount())
                .build();
    }
}
