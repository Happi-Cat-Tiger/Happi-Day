package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamInitService {

    private final TeamRepository teamRepository;
    private final DefaultImageUtils defaultImageUtils;

    public void initTeams() {
        String imageUrl = defaultImageUtils.getDefaultImageUrlTeamArtistProfile();

        List<Team> teams = List.of(
                Team.builder()
                        .name("동방신기")
                        .description("동방신기입니다.")
                        .logoUrl(imageUrl)
                        .build(),
                Team.builder()
                        .name("god")
                        .description("god입니다.")
                        .logoUrl(imageUrl)
                        .build()
        );

        teams.forEach(team -> {
            try {
                if (!teamRepository.existsByName(team.getName())) {
                    teamRepository.save(team);
                }
            } catch (Exception e) {
                log.error("DB Seeder 팀 저장 중 예외 발생 - 팀명: {}", team.getName(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_TEAM_SAVE_ERROR);
            }
        });

        // 추가적인 덤프 데이터
        for (int i = 1; i <= 40; i++) {
            String teamName = "팀 " + i;
            Team team = Team.builder()
                    .name(teamName)
                    .description(teamName + "입니다.")
                    .logoUrl(imageUrl)
                    .build();

            try {
                if (!teamRepository.existsByName(team.getName())) {
                    teamRepository.save(team);
                }
            } catch (Exception e) {
                log.error("DB Seeder 팀 저장 중 예외 발생 - 팀명: {}", team.getName(), e);
                throw new CustomException(ErrorCode.DB_SEEDER_TEAM_SAVE_ERROR);
            }
        }
    }
}
