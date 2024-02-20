package com.happiday.Happi_Day.domain.entity.team.dto;

import com.happiday.Happi_Day.domain.entity.team.Team;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamRegisterDto {
    private String name;
    private String description;
    private List<Long> artistIds;

    public Team toEntity() {
        return Team.builder()
                .name(name)
                .description(description)
                .artistTeamList(new ArrayList<>())
                .build();
    }
}
