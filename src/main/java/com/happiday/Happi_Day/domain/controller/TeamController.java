package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.product.dto.SalesListResponseDto;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamListResponseDto;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamRegisterDto;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamDetailResponseDto;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamUpdateDto;
import com.happiday.Happi_Day.domain.service.TeamService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    // 팀 등록
    @PostMapping
    public ResponseEntity<TeamDetailResponseDto> registerTeam(@RequestPart(name = "team") TeamRegisterDto requestDto,
                                                              @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        String username = SecurityUtils.getCurrentUsername();
        TeamDetailResponseDto responseDto = teamService.registerTeam(requestDto, imageFile);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 팀 정보 수정
    @PutMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponseDto> updateTeam(@PathVariable Long teamId,
                                                            @RequestPart(name = "team") TeamUpdateDto requestDto,
                                                            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        String username = SecurityUtils.getCurrentUsername();
        TeamDetailResponseDto responseDto = teamService.updateTeam(teamId, requestDto, imageFile, username);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 팀 삭제
    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long teamId) {
        String username = SecurityUtils.getCurrentUsername();
        teamService.deleteTeam(teamId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 팀 상세 조회
    @GetMapping("/{teamId}")
    public ResponseEntity<TeamDetailResponseDto> getTeam(@PathVariable Long teamId) {
        String username = SecurityUtils.getCurrentUsername();
        TeamDetailResponseDto responseDto = teamService.getTeam(teamId, username);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 팀 목록 조회
    @GetMapping
    public ResponseEntity<Slice<TeamListResponseDto>> getTeams(
            @PageableDefault(size = 24, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<TeamListResponseDto> slice = teamService.getTeams(pageable);
        return new ResponseEntity<>(slice, HttpStatus.OK);
    }

    // 팀 소속 아티스트 목록 조회
    @GetMapping("/{teamId}/artists")
    public ResponseEntity<List<ArtistListResponseDto>> getArtistsByTeam(@PathVariable Long teamId) {
        List<ArtistListResponseDto> teamArtists = teamService.getTeamArtists(teamId);
        return new ResponseEntity<>(teamArtists, HttpStatus.OK);
    }

    // 팀 관련 상품 목록 조회
    @GetMapping("/{teamId}/sales")
    public ResponseEntity<List<SalesListResponseDto>> getTeamSales(@PathVariable Long teamId) {
        List<SalesListResponseDto> responseDtos = teamService.getSalesList(teamId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    // 팀의 이벤트 목록 조회
    @GetMapping("/{teamId}/events")
    public ResponseEntity<List<EventListResponseDto>> getArtistEvents(@PathVariable Long teamId) {
        List<EventListResponseDto> responseDtos = teamService.getEvents(teamId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    // 팀 구독
    @PostMapping("/{teamId}/subscribe")
    public ResponseEntity<Void> subscribeToTeam(@PathVariable Long teamId) {
        String username = SecurityUtils.getCurrentUsername();
        teamService.subscribeToTeam(username, teamId);
        return ResponseEntity.ok().build();
    }

    // 팀 구독 취소
    @PostMapping("/{teamId}/unsubscribe")
    public ResponseEntity<Void> unsubscribeFromTeam(@PathVariable Long teamId) {
        String username = SecurityUtils.getCurrentUsername();
        teamService.unsubscribeFromTeam(username, teamId);
        return ResponseEntity.ok().build();
    }
}