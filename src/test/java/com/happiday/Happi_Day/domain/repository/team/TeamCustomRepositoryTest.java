package com.happiday.Happi_Day.domain.repository.team;

import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.repository.TeamCustomRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TeamCustomRepositoryTest {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamCustomRepository teamCustomRepository;

    @BeforeEach
    public void init() {
        // 테스트 데이터 준비
        teamRepository.save(Team.builder()
                .name("Test Team 1")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());
        teamRepository.save(Team.builder()
                .name("Test Team 2")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());
        teamRepository.save(Team.builder()
                .name("Test Team 3")
                .description("Test Description")
                .logoUrl("http://example.com/logo.jpg")
                .build());
    }

    @Test
    @DisplayName("팀 슬라이스 조회")
    public void findTeamsSliceTest() {
        // given
        Pageable pageable = PageRequest.of(0, 2); // 첫 페이지, 페이지 당 아이템 2개

        // when
        Slice<Team> teamSlice = teamCustomRepository.findTeamsSlice(pageable);

        // then
        assertThat(teamSlice.getContent()).hasSize(2); // 요청한 페이지 크기만큼 팀 로드
        assertThat(teamSlice.hasNext()).isTrue(); // 더 로드할 팀이 있어야 함
    }
}
