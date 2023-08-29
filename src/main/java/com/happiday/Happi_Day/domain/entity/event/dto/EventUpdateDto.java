package com.happiday.Happi_Day.domain.entity.event.dto;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.team.Team;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class EventUpdateDto {

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    private String location;

    private String thumbnailUrl;

    private String imageUrl;

    private List<Artist> artists;

    private List<Team> teams;

    public Event toEntity() {

        return Event.builder()
                .title(title)
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .artists(artists)
                .teams(teams)
                .startTime(startTime)
                .endTime(endTime)
                .description(description)
                .location(location)
                .build();
    }
}
