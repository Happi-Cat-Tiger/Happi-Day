package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistListResponseDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistRegisterDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistDetailResponseDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistUpdateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamListResponseDto;
import com.happiday.Happi_Day.domain.entity.product.dto.SalesListResponseDto;
import com.happiday.Happi_Day.domain.service.ArtistService;
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
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    // 아티스트 등록
    @PostMapping
    public ResponseEntity<ArtistDetailResponseDto> registerArtist(@RequestPart(name = "artist") ArtistRegisterDto requestDto,
                                                                  @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        String username = SecurityUtils.getCurrentUsername();
        ArtistDetailResponseDto responseDto = artistService.registerArtist(requestDto, imageFile);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    // 아티스트 정보 수정
    @PutMapping("/{artistId}")
    public ResponseEntity<ArtistDetailResponseDto> updateArtist(@PathVariable Long artistId,
                                                                @RequestPart(name = "artist") ArtistUpdateDto requestDto,
                                                                @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        String username = SecurityUtils.getCurrentUsername();
        ArtistDetailResponseDto responseDto = artistService.updateArtist(artistId, requestDto, imageFile);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 아티스트 삭제
    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
        String username = SecurityUtils.getCurrentUsername();
        artistService.delete(artistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 아티스트 상세 조회
    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDetailResponseDto> getArtist(@PathVariable Long artistId) {
        String username = SecurityUtils.getCurrentUsername();
        ArtistDetailResponseDto responseDto = artistService.getArtist(artistId, username);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 아티스트 목록 조회
    @GetMapping
    public ResponseEntity<Slice<ArtistListResponseDto>> getArtists(
            @PageableDefault(size = 24, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<ArtistListResponseDto> slice = artistService.getArtists(pageable);
        return new ResponseEntity<>(slice, HttpStatus.OK);
    }

    // 아티스트가 속한 팀 목록 조회
    @GetMapping("/{artistId}/teams")
    public ResponseEntity<List<TeamListResponseDto>> getArtistTeams(@PathVariable Long artistId) {
        List<TeamListResponseDto> artistTeams = artistService.getArtistTeams(artistId);
        return new ResponseEntity<>(artistTeams, HttpStatus.OK);
    }

    // 아티스트 관련 상품 목록 조회
    @GetMapping("/{artistId}/sales")
    public ResponseEntity<List<SalesListResponseDto>> getArtistSales(@PathVariable Long artistId) {
        List<SalesListResponseDto> responseDtos = artistService.getSalesList(artistId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    // 아티스트의 이벤트 목록 조회
    @GetMapping("/{artistId}/events")
    public ResponseEntity<List<EventListResponseDto>> getArtistEvents(@PathVariable Long artistId) {
        List<EventListResponseDto> responseDtos = artistService.getEvents(artistId);
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    // 아티스트 구독
    @PostMapping("/{artistId}/subscribe")
    public ResponseEntity<Void> subscribeToArtist(@PathVariable Long artistId) {
        String username = SecurityUtils.getCurrentUsername();
        artistService.subscribeToArtist(username, artistId);
        return ResponseEntity.ok().build();
    }

    // 아티스트 구독 취소
    @PostMapping("/{artistId}/unsubscribe")
    public ResponseEntity<Void> unsubscribeFromArtist(@PathVariable Long artistId) {
        String username = SecurityUtils.getCurrentUsername();
        artistService.unsubscribeFromArtist(username, artistId);
        return ResponseEntity.ok().build();
    }
}
