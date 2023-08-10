package com.happiday.Happi_Day.domain.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistRegisterDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistResponseDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistUpdateDto;
import com.happiday.Happi_Day.domain.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/artists")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;

    @PostMapping
    public ResponseEntity<ArtistResponseDto> registerArtist(@RequestPart(name = "artist") ArtistRegisterDto requestDto,
                                                            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        ArtistResponseDto responseDto = artistService.registerArtist(requestDto, imageFile);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{artistId}")
    public ResponseEntity<ArtistResponseDto> updateArtist(@PathVariable Long artistId,
                                                          @RequestPart(name = "artist") ArtistUpdateDto requestDto,
                                                          @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        ArtistResponseDto responseDto = artistService.updateArtist(artistId, requestDto, imageFile);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long artistId) {
        artistService.delete(artistId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistResponseDto> getArtist(@PathVariable Long artistId) {
        ArtistResponseDto responseDto = artistService.getArtist(artistId);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ArtistResponseDto>> getArtists() {
        List<ArtistResponseDto> responseDtos = artistService.getArtists();
        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }
}