package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.article.*;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadListArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.ReadOneArticleDto;
import com.happiday.Happi_Day.domain.entity.article.dto.WriteArticleDto;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final BoardCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ArtistRepository artistRepository;
    private final ArtistArticleRepository artistArticleRepository;
    private final TeamRepository teamRepository;
    private final TeamArticleRepository teamArticleRepository;
    private final HashtagRepository hashtagRepository;
    private final FileUtils fileUtils;
    private final RedisService redisService;
    private final QueryArticleRepository queryArticleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleHashtagRepository articleHashtagRepository;

    @Transactional
    public ReadOneArticleDto writeArticle(Long categoryId, WriteArticleDto dto, MultipartFile thumbnailImage, List<MultipartFile> imageFileList, String username) {
        // user 확인
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 카테고리
        BoardCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Article newArticle = Article.builder()
                .user(user)
                .category(category)
                .title(dto.getTitle())
                .content(dto.getContent())
                .articleHashtags(new ArrayList<>())
                .eventAddress(dto.getEventAddress())
                .articleLikes(new ArrayList<>())
                .articleComments(new ArrayList<>())
                .imageUrl(new ArrayList<>())
                .build();

        articleRepository.save(newArticle);

        List<ArtistArticle> artistArticleList = new ArrayList<>();
        List<TeamArticle> teamArticleList = new ArrayList<>();
        List<Hashtag> hashtags = new ArrayList<>();

        for (String keyword : dto.getHashtag()) {
            Optional<Artist> artist = artistRepository.findByName(keyword);
            if (artist.isPresent()) {
                ArtistArticle artistArticle = ArtistArticle.builder()
                        .article(newArticle)
                        .artist(artist.get())
                        .build();
                artistArticleRepository.save(artistArticle);
                artistArticleList.add(artistArticle);
                continue;
            }
            Optional<Team> team = teamRepository.findByName(keyword);
            if (team.isPresent()) {
                TeamArticle teamArticle = TeamArticle.builder()
                        .article(newArticle)
                        .team(team.get())
                        .build();
                teamArticleRepository.save(teamArticle);
                teamArticleList.add(teamArticle);
                continue;
            }
            Optional<Hashtag> hashtag = hashtagRepository.findByTag(keyword);
            if (hashtag.isPresent()) {
                hashtags.add(hashtag.get());
                continue;
            }
            Hashtag newHashtag = Hashtag.builder()
                    .tag(keyword)
                    .build();
            hashtagRepository.save(newHashtag);
            hashtags.add(newHashtag);
        }

        for (Hashtag hashtag: hashtags) {
            ArticleHashtag articleHashtag = ArticleHashtag.builder()
                    .hashtag(hashtag)
                    .article(newArticle)
                    .build();
            articleHashtagRepository.save(articleHashtag);
        }

        newArticle.setArtists(artistArticleList, teamArticleList);

        // 이미지 저장
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            String saveThumbnailUrl = fileUtils.uploadFile(thumbnailImage);
            newArticle.setThumbnailImage(saveThumbnailUrl);
        }
        if (imageFileList != null && !imageFileList.isEmpty()) {
            List<String> imageList = new ArrayList<>();
            for (MultipartFile image : imageFileList) {
                String imageUrl = fileUtils.uploadFile(image);
                imageList.add(imageUrl);
            }
            newArticle.setImageUrl(imageList);
        }

        articleRepository.save(newArticle);
        ReadOneArticleDto responseArticle = ReadOneArticleDto.fromEntity(newArticle);
        return responseArticle;
    }

    // 글 상세 조회
    @Transactional
    public ReadOneArticleDto readOne(String clientAddress, Long articleId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        if (redisService.isFirstIpRequest(clientAddress, articleId)) {
            log.debug("same user requests duplicate in 24hours: {}, {}", clientAddress, articleId);
            increaseViewCount(clientAddress, articleId);
        }

        return ReadOneArticleDto.fromEntity(article);
    }

    // 글 목록 조회
    public Page<ReadListArticleDto> readList(Long categoryId, Pageable pageable, String filter, String keyword) {
        BoardCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        Page<Article> articleList = queryArticleRepository.findArticleByFilterAndKeyword(pageable,categoryId, filter, keyword);
        return articleList.map(ReadListArticleDto::fromEntity);
    }

    // 전체글 구독중인 아티스트 글 조회
    public Page<ReadListArticleDto> readArticleBySubscribedArtists(Pageable pageable, Long id, String filter, String keyword, String username) {
        BoardCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        User user = userRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Article> articleList = queryArticleRepository.findArticleByFilterAndKeywordAndSubscribedArtists(pageable, filter, keyword, user);

        return articleList.map(ReadListArticleDto::fromEntity);
    }

    // 글 전체글 조회
    public Page<ReadListArticleDto> readList(Pageable pageable, String filter, String keyword) {
        Page<Article> articles = queryArticleRepository.findArticleByFilterAndKeyword(pageable,null, filter, keyword);
        return articles.map(ReadListArticleDto::fromEntity);

    }

    // 글 수정
    @Transactional
    public ReadOneArticleDto updateArticle(Long articleId, WriteArticleDto dto, String username, MultipartFile thumbnailImage, List<MultipartFile> imageFileList) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.equals(article.getUser())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        article.update(Article.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .eventAddress(dto.getEventAddress())
                .build()
        );

        List<ArtistArticle> artistArticleList = new ArrayList<>();
        List<TeamArticle> teamArticleList = new ArrayList<>();
        List<Hashtag> hashtags = new ArrayList<>();
        artistArticleRepository.deleteByArticle(article);
        teamArticleRepository.deleteByArticle(article);

        for (String keyword : dto.getHashtag()) {
            Optional<Artist> artist = artistRepository.findByName(keyword);
            if (artist.isPresent()) {
                article.getArtistArticleList().clear();
                ArtistArticle artistArticle = ArtistArticle.builder()
                        .article(article)
                        .artist(artist.get())
                        .build();
                artistArticleRepository.save(artistArticle);
                artistArticleList.add(artistArticle);
                continue;
            }
            Optional<Team> team = teamRepository.findByName(keyword);
            if (team.isPresent()) {
                article.getTeamArticleList().clear();
                TeamArticle teamArticle = TeamArticle.builder()
                        .article(article)
                        .team(team.get())
                        .build();
                teamArticleRepository.save(teamArticle);
                teamArticleList.add(teamArticle);
                continue;
            }
            Hashtag hashtag = hashtagRepository.findByTag(keyword)
                    .orElseGet(() -> {
                        Hashtag newHashtag = Hashtag.builder().tag(keyword).build();
                        hashtagRepository.save(newHashtag);
                        return newHashtag;
                    });
            hashtags.add(hashtag);
        }

        for (Hashtag hashtag: hashtags) {
            ArticleHashtag articleHashtag = ArticleHashtag.builder()
                    .hashtag(hashtag)
                    .article(article)
                    .build();
            articleHashtagRepository.save(articleHashtag);
        }

        article.setArtists(artistArticleList, teamArticleList);


        // 썸네일 이미지 저장
        if (thumbnailImage != null && !thumbnailImage.isEmpty()) {
            if (article.getThumbnailUrl() != null && !article.getThumbnailUrl().isEmpty()) {
                try {
                    fileUtils.deleteFile(article.getThumbnailUrl());
                    log.info("썸네일 이미지 삭제완료");
                } catch (Exception e) {
                    log.error("이미지 삭제 실패");
                }
            }
            String thumbnailImageUrl = fileUtils.uploadFile(thumbnailImage);
            article.setThumbnailImage(thumbnailImageUrl);
        }

        if (imageFileList != null && !imageFileList.isEmpty()) {
            if (article.getImageUrl() != null && !article.getImageUrl().isEmpty()) {
                try {
                    for (String url : article.getImageUrl()) {
                        fileUtils.deleteFile(url);
                        log.info("글 이미지 삭제 완료");
                    }
                } catch (Exception e) {
                    log.error("이미지 삭제 실패");
                }
            }
            List<String> imageList = new ArrayList<>();
            for (MultipartFile image : imageFileList) {
                String imageUrl = fileUtils.uploadFile(image);
                imageList.add(imageUrl);
            }
            article.setImageUrl(imageList);
        }

        articleRepository.save(article);

        return ReadOneArticleDto.fromEntity(article);
    }

    @Transactional
    public void deleteArticle(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!user.equals(article.getUser())) throw new CustomException(ErrorCode.FORBIDDEN);

        // 이미지 삭제
        if (article.getImageUrl() != null) {
            for (String imageUrl : article.getImageUrl()) {
                fileUtils.deleteFile(imageUrl);
            }
        }
        if (article.getThumbnailUrl() != null) {
            fileUtils.deleteFile(article.getThumbnailUrl());
        }

        List<ArticleComment> articleComments = articleCommentRepository.findAllByArticle(article);
        for (ArticleComment comment : articleComments) {
            articleCommentRepository.delete(comment);
        }

        articleRepository.deleteById(articleId);

        // 게시글 - 팀/아티스트 연관 관계 삭제
        artistArticleRepository.deleteByArticle(article);
        teamArticleRepository.deleteByArticle(article);
    }

    @Transactional
    public String likeArticle(Long articleId, String username) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new CustomException(ErrorCode.ARTICLE_NOT_FOUND));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String response = "";
        Optional<ArticleLike> articleLike = articleLikeRepository.findByUserAndArticle(user, article);

        if (articleLike.isPresent()) {
            articleLikeRepository.delete(articleLike.get());
            List<ArticleLike> articleLikeUser = articleLikeRepository.findByArticle(article);
            response = "좋아요가 취소되었습니다. 현재 좋아요 수 : " + articleLikeUser.size();
        } else {
            ArticleLike newArticleLike = ArticleLike.builder()
                    .article(article)
                    .user(user)
                    .build();
            articleLikeRepository.save(newArticleLike);
            List<ArticleLike> articleLikeUser = articleLikeRepository.findByArticle(article);
            response = "좋아요를 눌렀습니다. 현재 좋아요 수 : " + articleLikeUser.size();
        }
        return response;
    }

    @Transactional
    public void increaseViewCount(String clientAddress, Long articleId) {
        articleRepository.increaseViewCount(articleId);
        redisService.clientRequest(clientAddress, articleId);
    }


}
