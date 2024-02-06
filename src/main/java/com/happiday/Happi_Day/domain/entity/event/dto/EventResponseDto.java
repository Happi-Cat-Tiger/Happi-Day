package com.happiday.Happi_Day.domain.entity.event.dto;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class EventResponseDto {

    private Long id;

    private String username;

    private String title;

    private LocalDateTime updatedAt;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String address;

    private String location;

    private String thumbnailUrl;

    private String imageUrl;

    private List<String> artists;

    private List<String> teams;

    private List<String> hashtags;

    private int commentCount;

    private int likeCount;

    private int joinCount;

    private int viewCount;

    private String userProfileUrl;


    public static EventResponseDto fromEntity(Event event) {

        return EventResponseDto.builder()
                .id(event.getId())
                .username(event.getUser().getNickname())
                .title(event.getTitle())
                .updatedAt(event.getUpdatedAt())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .description(event.getDescription())
                .address(event.getAddress())
                .location(event.getLocation())
                .thumbnailUrl(event.getThumbnailUrl())
                .imageUrl(event.getImageUrl())
                .artists(event.getArtistsEventList().stream().map(ArtistEvent::getArtist).map(Artist::getName).collect(Collectors.toList()))
                .teams(event.getTeamsEventList().stream().map(TeamEvent::getTeam).map(Team::getName).collect(Collectors.toList()))
                .hashtags(event.getEventHashtags().stream().map(EventHashtag::getHashtag).map(Hashtag::getTag).collect(Collectors.toList()))
                .commentCount(event.getCommentCount())
                .joinCount(event.getJoinCount())
                .likeCount(event.getLikeCount())
                .viewCount(event.getViewCount())
                .userProfileUrl(event.getUser().getImageUrl())
                .build();
    }
}
