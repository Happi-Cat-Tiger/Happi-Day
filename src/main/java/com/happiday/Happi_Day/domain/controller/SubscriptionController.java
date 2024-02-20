package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.artist.dto.ArtistListResponseDto;
import com.happiday.Happi_Day.domain.entity.subscription.dto.*;
import com.happiday.Happi_Day.domain.entity.team.dto.TeamListResponseDto;
import com.happiday.Happi_Day.domain.service.SubscriptionService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // 구독 페이지 조회 (구독 중 팀/아티스트 + 구독 중이지 않은 팀/아티스트 목록 조회)
    @GetMapping("/overview")
    public ResponseEntity<CombinedSubscriptionsDto> getSubscriptionOverview(Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        CombinedSubscriptionsDto combinedSubscriptions = subscriptionService.getSubscriptionsWithUnsubscribed(username, pageable);
        return ResponseEntity.ok(combinedSubscriptions);
    }

    // 현재 구독 중인 팀/아티스트 목록 조회
    @GetMapping
    public ResponseEntity<SubscriptionsResponseDto> getSubscriptions() {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(subscriptionService.getSubscriptions(username));
    }

    // 현재 구독 중인 아티스트 목록 조회
    @GetMapping("/artists")
    public ResponseEntity<SubscriptionArtistsDto> getSubscribedTeams() {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(subscriptionService.getSubscribedArtists(username));
    }

    // 현재 구독 중인 팀 목록 조회
    @GetMapping("/teams")
    public ResponseEntity<SubscriptionTeamsDto> getSubscribedArtists() {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(subscriptionService.getSubscribedTeams(username));
    }

    // 구독 추가
    @PostMapping
    public ResponseEntity<Void> addSubscription(@RequestBody SubscriptionRequestDto requestDto) {
        String username = SecurityUtils.getCurrentUsername();
        subscriptionService.addSubscription(username, requestDto);
        return ResponseEntity.ok().build();
    }

    // 구독 취소
    @DeleteMapping
    public ResponseEntity<Void> cancelSubscription(
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long teamId) {
        String username = SecurityUtils.getCurrentUsername();
        subscriptionService.cancelSubscription(username, artistId, teamId);
        return ResponseEntity.ok().build();
    }

    // 구독하지 않은 아티스트 조회
    @GetMapping("/undiscovered/artists")
    public ResponseEntity<Page<ArtistListResponseDto>> getUnsubscribedArtists(Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(subscriptionService.getUnSubscribedArtists(username, pageable));
    }

    // 구독하지 않은 팀 조회
    @GetMapping("/undiscovered/teams")
    public ResponseEntity<Page<TeamListResponseDto>> getUnsubscribedTeams(Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return ResponseEntity.ok(subscriptionService.getUnSubscribedTeams(username, pageable));
    }
}
