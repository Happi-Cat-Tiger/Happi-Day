package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.article.dto.ReadListArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadListCommentDto;
import com.happiday.Happi_Day.domain.entity.event.dto.EventListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.comment.EventCommentListResponseDto;
import com.happiday.Happi_Day.domain.entity.event.dto.review.EventReviewResponseDto;
import com.happiday.Happi_Day.domain.entity.product.dto.ReadListOrderDto;
import com.happiday.Happi_Day.domain.entity.product.dto.ReadListSalesDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserPWDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserResponseDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserUpdateDto;
import com.happiday.Happi_Day.domain.service.MyPageService;
import com.happiday.Happi_Day.domain.service.UserService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final MyPageService myPageService;

    @GetMapping("/info")
    public ResponseEntity<UserResponseDto> getUser() {
        String username = SecurityUtils.getCurrentUsername();
        UserResponseDto myProfile = userService.getUserProfile(username);
        return new ResponseEntity<>(myProfile,HttpStatus.OK);
    }
    @PatchMapping("/info")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserUpdateDto dto) {
        String username = SecurityUtils.getCurrentUsername();
        UserResponseDto newProfile = userService.updateUserProfile(username, dto);
        return new ResponseEntity<>(newProfile,HttpStatus.OK);
    }

    @PatchMapping("/info/image")
    public ResponseEntity<UserResponseDto> changeImage(@RequestPart("multipartFile") MultipartFile multipartFile) {
        String username = SecurityUtils.getCurrentUsername();
        UserResponseDto profile = userService.changeImage(username, multipartFile);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PatchMapping("/info/default")
    public ResponseEntity<UserResponseDto> resetImage() {
        String username = SecurityUtils.getCurrentUsername();
        UserResponseDto profile = userService.resetImage(username);
        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @DeleteMapping("/withdrawal")
    public ResponseEntity<String> withdrawUser(@RequestBody UserPWDto dto) {
        String username = SecurityUtils.getCurrentUsername();
        userService.deleteUser(username, dto);
        return new ResponseEntity<>("회원탈퇴되었습니다.", HttpStatus.OK);
    }

    @GetMapping("/articles")
    public ResponseEntity<Page<ReadListArticleDto>> getMyArticles(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMyArticles(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/articles/comments")
    public ResponseEntity<Page<ReadListCommentDto>> getMyArticleComments(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMyArticleComments(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/articles/like")
    public ResponseEntity<Page<ReadListArticleDto>> getLikeArticles(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readLikeArticles(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/events")
    public ResponseEntity<Page<EventListResponseDto>> getMyEvents(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMyEvents(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/events/comments")
    public ResponseEntity<Page<EventCommentListResponseDto>> getMyEventComments(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMyEventComments(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/events/like")
    public ResponseEntity<Page<EventListResponseDto>> getLikeEvents(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readLikeEvents(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/events/join")
    public ResponseEntity<Page<EventListResponseDto>> getJoinEvents(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readJoinEvents(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/reviews")
    public ResponseEntity<Page<EventReviewResponseDto>> getMyReviews(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.getMyReviews(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/sales")
    public ResponseEntity<Page<ReadListSalesDto>> getMySales(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMySales(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/sales/like")
    public ResponseEntity<Page<ReadListSalesDto>> getLikeSales(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readLikeSales(username, pageable), HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<ReadListOrderDto>> getMyOrders(@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        String username = SecurityUtils.getCurrentUsername();
        return new ResponseEntity<>(myPageService.readMyOrders(username, pageable), HttpStatus.OK);
    }

}
