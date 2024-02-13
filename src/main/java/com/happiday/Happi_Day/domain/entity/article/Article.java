package com.happiday.Happi_Day.domain.entity.article;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.artist.ArtistArticle;
import com.happiday.Happi_Day.domain.entity.board.BoardCategory;
import com.happiday.Happi_Day.domain.entity.team.TeamArticle;
import com.happiday.Happi_Day.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "article")
@SQLDelete(sql = "UPDATE article SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Article extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 유저 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Setter
    private String thumbnailUrl;

    @Setter
    @ElementCollection
    private List<String> imageUrl = new ArrayList<>();

    @Column
    private String ectTeams;

    @Column
    private String ectArtists;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    // 카테고리 홍보 에만 필요
    private String eventAddress;

    // 게시글 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BoardCategory category;

    // 댓글 매핑
    @OneToMany(mappedBy = "article")
    private List<ArticleComment> articleComments = new ArrayList<>();

    // 게시글 좋아요 매핑
    @OneToMany(mappedBy = "article")
    private List<ArticleLike> articleLikes = new ArrayList<>();

    // 게시글 아티스트 매핑
    @OneToMany(mappedBy = "article")
    private List<ArtistArticle> artistArticleList = new ArrayList<>();

    // 게시글 팀 매핑
    @OneToMany(mappedBy = "article")
    private List<TeamArticle> teamArticleList = new ArrayList<>();

    // 해시태그 매핑
    @Setter
    @OneToMany(mappedBy = "article")
    private List<ArticleHashtag> articleHashtags = new ArrayList<>();

    public void update(Article updateArticle) {
        if (updateArticle.getTitle() != null) this.title = updateArticle.getTitle();
        if (updateArticle.getContent() != null) this.content = updateArticle.getContent();
        if (updateArticle.getEventAddress() != null) this.eventAddress = updateArticle.getEventAddress();
        if (updateArticle.getArtistArticleList() != null) this.artistArticleList = updateArticle.getArtistArticleList();
        if (updateArticle.getTeamArticleList() != null) this.teamArticleList = updateArticle.getTeamArticleList();
        if (updateArticle.getEctArtists() != null) this.ectArtists = updateArticle.getEctArtists();
        if (updateArticle.getEctTeams() != null) this.ectTeams = updateArticle.getEctTeams();
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailUrl = thumbnailImage;
    }

    public void setArtists(List<ArtistArticle> artistArticleList, List<TeamArticle> teamArticleList) {
        if (artistArticleList != null) this.artistArticleList = artistArticleList;
        if (teamArticleList != null) this .teamArticleList = teamArticleList;
    }
}
