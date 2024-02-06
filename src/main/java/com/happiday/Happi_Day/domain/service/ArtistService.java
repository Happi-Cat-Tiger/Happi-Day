package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistEvent;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistListResponseDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistRegisterDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistDetailResponseDto;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistUpdateDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamListResponseDto;
import com.happiday.Happi_Day.domain.entity.product.dto.SalesListResponseDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ArtistRepository;
import com.happiday.Happi_Day.domain.repository.ArtistSubscriptionRepository;
import com.happiday.Happi_Day.domain.repository.TeamRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.DefaultImageUtils;
import com.happiday.Happi_Day.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ArtistSubscriptionRepository subscriptionRepository;
    private final FileUtils fileUtils;
    private final DefaultImageUtils defaultImageUtils;

    // 아티스트 등록
    @Transactional
    public ArtistDetailResponseDto registerArtist(ArtistRegisterDto requestDto, MultipartFile imageFile) {
        Artist artistEntity = requestDto.toEntity();

        // 이미지 저장 로직
        if (imageFile != null && !imageFile.isEmpty()) {
            String saveFileUrl = fileUtils.uploadFile(imageFile);
            artistEntity.setProfileUrl(saveFileUrl);
        } else {
            artistEntity.setProfileUrl(defaultImageUtils.getDefaultImageUrlTeamArtistProfile());
        }

        // 팀과의 연관 관계 처리
        if (requestDto.getTeamIds() != null && !requestDto.getTeamIds().isEmpty()) {
            List<Team> teams = teamRepository.findAllById(requestDto.getTeamIds());

            artistEntity.setTeams(teams); // 아티스트와 팀 연결
            artistEntity = artistRepository.save(artistEntity);
            return ArtistDetailResponseDto.of(artistEntity, false, teamsToDto(artistEntity.getTeams()));
        }

        artistEntity = artistRepository.save(artistEntity);
        return ArtistDetailResponseDto.of(artistEntity, false, new ArrayList<>());
    }

    // 아티스트 정보 수정
    @Transactional
    public ArtistDetailResponseDto updateArtist(Long artistId, ArtistUpdateDto requestDto, MultipartFile imageFile) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        // 이미지 저장 및 기존 이미지 삭제 로직
        if (imageFile != null && !imageFile.isEmpty()) {
            // 기존 이미지가 있다면 삭제
            if (artist.getProfileUrl() != null && !artist.getProfileUrl().isEmpty()) {
                try {
                    fileUtils.deleteFile(artist.getProfileUrl());
                    log.info("이미지 삭제 완료: " + artist.getProfileUrl());
                } catch (Exception e) {
                    log.error("이미지 삭제 실패: " + artist.getProfileUrl(), e);
                    throw new CustomException(ErrorCode.FILE_DELETE_BAD_REQUEST);
                }
            }

            // 새로운 이미지 업로드
            String saveFileUrl = fileUtils.uploadFile(imageFile);
            artist.setProfileUrl(saveFileUrl);
            log.info("이미지 업데이트: " + saveFileUrl);
        }

        artist.update(requestDto.toEntity());

        // 팀과의 연관 관계 처리
        if (requestDto.getTeamIds() != null) {
            List<Team> teams = teamRepository.findAllById(requestDto.getTeamIds());
            artist.setTeams(teams);

            List<TeamListResponseDto> teamDtos = artist.getTeams().stream()
                    .map(TeamListResponseDto::of)
                    .collect(Collectors.toList());
            artistRepository.save(artist);
            return ArtistDetailResponseDto.of(artist, false, teamDtos);
        }

        artistRepository.save(artist);
        return ArtistDetailResponseDto.of(artist, false, new ArrayList<>());
    }

    // 아티스트 삭제
    @Transactional
    public void delete(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));
        artistRepository.delete(artist);
    }

    // 아티스트 상세 조회
    public ArtistDetailResponseDto getArtist(Long artistId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        // 구독 여부 확인
        boolean isSubscribed = subscriptionRepository.existsByUserAndArtist(user, artist);

        // 아티스트가 속한 팀 정보 가져오기
        List<TeamListResponseDto> teams = artist.getTeams().stream()
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());

        // 관련 상품 조회
        List<SalesListResponseDto> sales = artist.getSalesList().stream()
                .map(SalesListResponseDto::of)
                .collect(Collectors.toList());

        // 관련 이벤트 조회
        List<EventListResponseDto> events = artist.getEvents().stream()
                .map(ArtistEvent::getEvent)
                .map(EventListResponseDto::fromEntity)
                .collect(Collectors.toList());

        return ArtistDetailResponseDto.of(artist, isSubscribed, teams, sales, events);
    }

    // 아티스트 목록 조회
    public List<ArtistListResponseDto> getArtists() {
        return artistRepository.findAll().stream()
                .map(ArtistListResponseDto::of)
                .collect(Collectors.toList());
    }

    // 아티스트가 속한 팀 목록 조회
    public List<TeamListResponseDto> getArtistTeams(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        return artist.getTeams().stream()
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());
    }

    // 아티스트 관련 상품 목록 조회
    public List<SalesListResponseDto> getSalesList(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        return artist.getSalesList().stream()
                .map(SalesListResponseDto::of)
                .collect(Collectors.toList());
    }

    // 아티스트 이벤트 목록 조회
    public List<EventListResponseDto> getEvents(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        return artist.getEvents().stream()
                .map(ArtistEvent::getEvent)
                .map(EventListResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 아티스트 구독 추가
    @Transactional
    public void subscribeToArtist(String username, Long artistId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        boolean isAlreadySubscribed = subscriptionRepository.existsByUserAndArtist(user, artist);

        if (!isAlreadySubscribed) {
            ArtistSubscription subscription = ArtistSubscription.builder()
                    .user(user)
                    .artist(artist)
                    .subscribedAt(LocalDateTime.now())
                    .build();
            subscriptionRepository.save(subscription);
        }
    }

    // 아티스트 구독 취소
    @Transactional
    public void unsubscribeFromArtist(String username, Long artistId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

        ArtistSubscription artistSubscription = subscriptionRepository.findByUserAndArtist(user, artist)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        subscriptionRepository.delete(artistSubscription);
    }

    // 팀을 DTO로 변환하는 유틸리티 메서드
    private List<TeamListResponseDto> teamsToDto(List<Team> teams) {
        return teams.stream()
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());
    }
}
