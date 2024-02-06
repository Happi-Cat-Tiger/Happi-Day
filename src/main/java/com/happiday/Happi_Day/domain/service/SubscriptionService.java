package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistListResponseDto;
import com.happiday.Happi_Day.domain.entity.subscription.dto.CombinedSubscriptionsDto;
import com.happiday.Happi_Day.domain.entity.subscription.dto.SubscriptionRequestDto;
import com.happiday.Happi_Day.domain.entity.subscription.dto.SubscriptionsResponseDto;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamListResponseDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final ArtistSubscriptionRepository artistSubscriptionRepository;
    private final TeamSubscriptionRepository teamSubscriptionRepository;
    private final TeamRepository teamRepository;

    // 구독 페이지 조회 (구독 중 팀/아티스트 + 구독 중이지 않은 팀/아티스트 목록 조회)
    public CombinedSubscriptionsDto getSubscriptionsWithUnsubscribed(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 구독 중인 아티스트와 팀
        List<ArtistListResponseDto> subscribedArtists = user.getArtistSubscriptionList().stream()
                .map(ArtistSubscription::getArtist)
                .map(ArtistListResponseDto::of)
                .collect(Collectors.toList());
        List<TeamListResponseDto> subscribedTeams = user.getTeamSubscriptionList().stream()
                .map(TeamSubscription::getTeam)
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());

        // 구독하지 않은 아티스트와 팀
        List<ArtistListResponseDto> unsubscribedArtists = artistRepository.findUnsubscribedArtists(user.getId(), pageable).stream()
                .map(ArtistListResponseDto::of)
                .collect(Collectors.toList());
        List<TeamListResponseDto> unsubscribedTeams = teamRepository.findUnsubscribedTeams(user.getId(), pageable).stream()
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());

        return new CombinedSubscriptionsDto(subscribedArtists, subscribedTeams, unsubscribedArtists, unsubscribedTeams);
    }

    // 현재 구독 중인 팀/아티스트 목록 조회
    @Transactional(readOnly = true)
    public SubscriptionsResponseDto getCurrentSubscriptions(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<ArtistListResponseDto> subscribedArtists = user.getArtistSubscriptionList().stream()
                .map(ArtistSubscription::getArtist)
                .map(ArtistListResponseDto::of)
                .collect(Collectors.toList());

        List<TeamListResponseDto> subscribedTeams = user.getTeamSubscriptionList().stream()
                .map(TeamSubscription::getTeam)
                .map(TeamListResponseDto::of)
                .collect(Collectors.toList());

        return new SubscriptionsResponseDto(subscribedArtists, subscribedTeams);
    }

    // 구독 추가
    @Transactional
    public void addSubscription(String username, SubscriptionRequestDto requestDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (requestDto.getArtistId() != null) {
            Artist artist = artistRepository.findById(requestDto.getArtistId())
                    .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

            boolean isAlreadySubscribed = artistSubscriptionRepository.existsByUserAndArtist(user, artist);

            if (!isAlreadySubscribed) {
                ArtistSubscription subscription = ArtistSubscription.builder()
                        .user(user)
                        .artist(artist)
                        .build();
                artistSubscriptionRepository.save(subscription);
            }
        }

        if (requestDto.getTeamId() != null) {
            Team team = teamRepository.findById(requestDto.getTeamId())
                    .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

            boolean isAlreadySubscribed = teamSubscriptionRepository.existsByUserAndTeam(user, team);

            if (!isAlreadySubscribed) {
                TeamSubscription subscription = TeamSubscription.builder()
                        .user(user)
                        .team(team)
                        .build();
                teamSubscriptionRepository.save(subscription);
            }
        }

        userRepository.save(user);
    }

    // 구독 취소
    @Transactional
    public void cancelSubscription(String username, Long artistId, Long teamId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (artistId != null) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ARTIST_NOT_FOUND));

            ArtistSubscription artistSubscription = artistSubscriptionRepository.findByUserAndArtist(user, artist)
                    .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

            artistSubscriptionRepository.delete(artistSubscription);
        }

        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

            TeamSubscription teamSubscription = teamSubscriptionRepository.findByUserAndTeam(user, team)
                    .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

            teamSubscriptionRepository.delete(teamSubscription);
        }

        userRepository.save(user);
    }

    // 구독하지 않은 아티스트 조회
    public Page<ArtistListResponseDto> getSubscribedArtists(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return artistRepository.findUnsubscribedArtists(user.getId(), pageable)
                .map(ArtistListResponseDto::of);
    }

    // 구독하지 않은 팀 조회
    public Page<TeamListResponseDto> getUnSubscribedTeams(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return teamRepository.findUnsubscribedTeams(user.getId(), pageable)
                .map(TeamListResponseDto::of);
    }
}
