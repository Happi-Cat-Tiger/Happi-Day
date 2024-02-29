package com.happiday.Happi_Day.domain.entity.artist.dto;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArtistRegisterDto {
    private String name;
    private String description;
    private List<Long> teamIds;

    public Artist toEntity() {
        return Artist.builder()
                .name(name)
                .description(description)
                .artistTeamList(new ArrayList<>())
                .build();
    }
}
