package com.happiday.Happi_Day.domain.controller;

import com.happiday.Happi_Day.domain.entity.article.dto.ReadListArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadOneArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.WriteArticleDto;
import com.happiday.Happi_Day.domain.service.ArticleService;
import com.happiday.Happi_Day.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    // 글 작성
    @PostMapping(value = "/{categoryId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReadOneArticleDto> writeArticle(
            @PathVariable("categoryId") Long id,
            @Valid @RequestPart(value = "article") WriteArticleDto requestDto,
            @RequestPart(value = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFileList) {
        String username = SecurityUtils.getCurrentUsername();
        ReadOneArticleDto response = articleService.writeArticle(id, requestDto, thumbnailImage, imageFileList, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // 글 상세 조회
    @GetMapping("/{articleId}")
    public ResponseEntity<ReadOneArticleDto> readOne(HttpServletRequest request, @PathVariable("articleId") Long id) {
        log.info("글 상세 조회");
        String clientAddress = request.getRemoteAddr();
        ReadOneArticleDto responseDto = articleService.readOne(clientAddress, id);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 글 목록 조회
    @GetMapping("/{categoryId}/list")
    public ResponseEntity<Page<ReadListArticleDto>> readList(
            @PathVariable("categoryId") Long categoryId,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword) {
        Page<ReadListArticleDto> responseArticles = articleService.readList(categoryId, pageable, filter, keyword);
        return new ResponseEntity<>(responseArticles, HttpStatus.OK);
    }

    // 구독중인 아티스트/팀 게시글 조회
    @GetMapping("{categoryId}/list/subscribedArtists")
    public ResponseEntity<Page<ReadListArticleDto>> readArticleBySubscribedArtists(
            @PathVariable("categoryId") Long id,
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword
    ){
        String username = SecurityUtils.getCurrentUsername();

        Page<ReadListArticleDto> responseDtoList = articleService.readArticleBySubscribedArtists(pageable, id, filter, keyword, username);
        return new ResponseEntity<>(responseDtoList, HttpStatus.OK);
    }

    // 글 전체글 조회
    @GetMapping()
    public ResponseEntity<Page<ReadListArticleDto>> readAllArticles(
            @PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String keyword){
        Page<ReadListArticleDto> responseArticles = articleService.readList(pageable, filter, keyword);
        return new ResponseEntity<>(responseArticles, HttpStatus.OK);
    }

    // 글 수정
    @PutMapping("/{articleId}")
    public ResponseEntity<ReadOneArticleDto> updateArticle(
            @PathVariable("articleId") Long articleId,
            @RequestPart(name = "article") WriteArticleDto requestDto,
            @RequestPart(name = "thumbnailImage", required = false) MultipartFile thumbnailImage,
            @RequestPart(name = "imageFile", required = false) List<MultipartFile> imageFileList) {
        String username = SecurityUtils.getCurrentUsername();
        ReadOneArticleDto responseArticle = articleService.updateArticle(articleId, requestDto, username, thumbnailImage, imageFileList);
        return new ResponseEntity<>(responseArticle, HttpStatus.OK);
    }

    // 글 삭제
    @DeleteMapping("/{articleId}")
    public ResponseEntity<String> deleteArticle(
            @PathVariable("articleId") Long articleId) {
        String username = SecurityUtils.getCurrentUsername();
        articleService.deleteArticle(articleId, username);
        return new ResponseEntity<>("삭제 성공", HttpStatus.OK);
    }

    // 글 좋아요
    @PostMapping("/{articleId}/like")
    public ResponseEntity<String> likeArticle(
            @PathVariable("articleId") Long articleId) {
        String username = SecurityUtils.getCurrentUsername();
        String response = articleService.likeArticle(articleId, username);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
