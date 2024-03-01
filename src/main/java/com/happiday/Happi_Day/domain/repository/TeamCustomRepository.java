package com.happiday.Happi_Day.domain.repository;

import com.happiday.Happi_Day.domain.entity.team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TeamCustomRepository {
    Slice<Team> findTeamsSlice(Pageable pageable);
}
