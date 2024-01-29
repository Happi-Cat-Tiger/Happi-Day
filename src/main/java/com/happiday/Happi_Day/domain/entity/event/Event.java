package com.happiday.Happi_Day.domain.entity.event;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name ="event")
@SQLDelete(sql = "UPDATE event SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Event extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String address;

    @Setter
    private String thumbnailUrl;

    @Setter
    private String imageUrl;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    // 이벤트 댓글 관계 설정
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventComment> comments;

    // 이벤트 리뷰 관계 설정
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<EventReview> reviews;

    // 이벤트 좋아요 매핑
    @ManyToMany(mappedBy = "eventLikes")
    private List<User> likes = new ArrayList<>();

    // 이벤트 참여하기 매핑
    @ManyToMany(mappedBy = "eventJoinList")
    private List<User> joinList = new ArrayList<>();

    // 이벤트 팀 매핑
    @ManyToMany
    @JoinTable(
            name = "event_team",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    // 이벤트 아티스트 매핑
    @ManyToMany
    @JoinTable(
            name = "event_artist",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists = new ArrayList<>();

    // 이벤트 해시태그 매핑
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "event_hashtag",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    private List<Hashtag> hashtags = new ArrayList<>();




    public void update(Event updateEvent) {
        if (updateEvent.getTitle() != null &&  !updateEvent.getTitle().isEmpty()) {
            this.title = updateEvent.getTitle();
        }
        if (updateEvent.getStartTime() != null) {
            this.startTime = updateEvent.getStartTime();
        }
        if (updateEvent.getEndTime() != null) {
            this.endTime = updateEvent.getEndTime();
        }
        if (updateEvent.getDescription() != null && !updateEvent.getDescription().isEmpty()) {
            this.description = updateEvent.getDescription();
        }
        if (updateEvent.getAddress() != null) {
            this.address = updateEvent.getAddress();
        }
        if (updateEvent.getLocation() != null) {
            this.location = updateEvent.getLocation();
        }
        if (updateEvent.getTeams() != null && !updateEvent.getTeams().isEmpty()) {
            this.teams.clear();
            this.teams = updateEvent.getTeams();
        }
        if (updateEvent.getArtists() != null && !updateEvent.getArtists().isEmpty()) {
            this.artists.clear();
            this.artists = updateEvent.getArtists();
        }
        if (updateEvent.getHashtags() != null && !updateEvent.getHashtags().isEmpty()) {
            this.hashtags.clear();
            this.hashtags = updateEvent.getHashtags();
        }
    }

    public int getLikeCount() {
        return likes != null ? likes.size() : 0;
    }

    public int getJoinCount() {
        return joinList != null ? joinList.size() : 0;
    }

    public int getCommentCount() {
        return comments != null ? comments.size() : 0;
    }

    public int getReviewCount() { return reviews != null ? reviews.size() : 0;}
}
