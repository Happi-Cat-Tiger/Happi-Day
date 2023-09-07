package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadListArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadOneArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.WriteArticleDto;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final BoardCategoryRepository categoryRepository;

    @Transactional
    public Article writeArticle(Long categoryId, WriteArticleDto dto, MultipartFile image) throws IOException {
        // 아티스트 목록
        List<String> artistList = Arrays.asList(dto.getArtists().replace(" ", "").split("#"));
        // 팀 목록
        List<String> teamList = Arrays.asList(dto.getTeams().replace(" ", "").split("#"));

        // 해시태그
        List<String> hashtags = Arrays.asList(dto.getHashtag().replace(" ", "").split("#"));
        List<Hashtag> hashtagList = new ArrayList<>();
        for (String hashtag : hashtags) {
            Hashtag newHashtag = Hashtag.builder()
                    .tag(hashtag)
                    .build();
            hashtagList.add(newHashtag);
        }

        // 카테고리
        BoardCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // TODO 정보 저장 : 유저, 댓글, 좋아요, 스크랩, 아티스트 리스트, 팀 리스트 추가예정
        Article newArticle = Article.builder()
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .eventAddress(dto.getEventAddress())
                .hashtags(hashtagList)
                .build();
        // 썸네일 이미지 저장
        if (image != null) {
            Files.createDirectories(Path.of(String.format("image/%d", newArticle.getId())));
            Path path = Path.of(String.format("image/%d/thumbnail.png", newArticle.getId()));

            image.transferTo(path);
        }
        return articleRepository.save(newArticle);
    }


    // 글 상세 조회
    // TODO user, 댓글, 좋아요, 스크랩 정보 추가 예정
    public ReadOneArticleDto readOne(Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ReadOneArticleDto.fromEntity(article);
    }

    // 글 목록 조회
    public List<ReadListArticleDto> readList(Long categoryId, String filter) {
        BoardCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        List<ReadListArticleDto> articles = articleRepository.findAllByCategory(category);
        // TODO 필터 적용 추가 예정

        return articles;
    }

    // 글 수정
    @Transactional
    public Article updateArticle(Long articleId, WriteArticleDto dto, MultipartFile image) throws IOException {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // 아티스트 목록
        List<String> artistList = Arrays.asList(dto.getArtists().replace(" ", "").split("#"));
        // 팀 목록
        List<String> teamList = Arrays.asList(dto.getTeams().replace(" ", "").split("#"));

        // 해시태그
        List<String> hashtags = Arrays.asList(dto.getHashtag().replace(" ", "").split("#"));
        List<Hashtag> hashtagList = new ArrayList<>();
        for (String hashtag : hashtags) {
            Hashtag newHashtag = Hashtag.builder()
                    .tag(hashtag)
                    .build();
            hashtagList.add(newHashtag);
        }

        // TODO 아티스트 리스트, 팀 리스트 추가예정
        article.update(dto.toEntity());

        // 썸네일 이미지 수정
        if (image != null) {
            Path path = Path.of(String.format("image/%d/thumbnail.png", article.getId()));
            image.transferTo(path);
        }

        return articleRepository.save(article);
    }

    public void deleteArticle(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        if (!optionalArticle.isEmpty()) {
            articleRepository.deleteById(articleId);
        }
    }
}