package com.happiday.Happi_Day.domain.entity.user;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.article.Article;
import com.happiday.Happi_Day.domain.entity.article.ArticleComment;
import com.happiday.Happi_Day.domain.entity.article.ArticleLike;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSubscription;
import com.happiday.Happi_Day.domain.entity.chat.ChatMessage;
import com.happiday.Happi_Day.domain.entity.chat.ChatRoom;
import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.event.EventLike;
import com.happiday.Happi_Day.domain.entity.event.EventParticipation;
import com.happiday.Happi_Day.domain.entity.event.EventReview;
import com.happiday.Happi_Day.domain.entity.product.Order;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.SalesLike;
import com.happiday.Happi_Day.domain.entity.team.Team;
import com.happiday.Happi_Day.domain.entity.team.TeamSubscription;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "user")
@EntityListeners(value = AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE user SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 유저 식별 ID

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private String realname;

    @Column(nullable = false, unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    private Boolean isActive;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLoginAt; // 마지막 로그인 날짜

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime termsAt; // 약관 동의 날짜

    @Column(nullable = false)
    private Boolean isTermsAgreed = false; // 약관 동의 여부

    private String imageUrl;

    // 활성화 상태구분 & 탈퇴, 관리자에 의한 삭제 구분
    @PrePersist
    public void prePersist() {
        this.isActive = this.isActive == null || this.isActive;
//        this.isDeleted = this.isDeleted != null && this.isDeleted;
    }

    // 게시글 작성 매핑(Article) => 커뮤니티(자유게시글)
    @OneToMany(mappedBy = "user")
    private List<Article> articles = new ArrayList<>();

//    // 게시글 좋아요 매핑
//    @ManyToMany
//    @JoinTable(
//            name = "user_article_likes",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "article_id")
//    )
//    private List<Article> articleLikes = new ArrayList<>();

    // 게시글 좋아요 매핑
    @OneToMany(mappedBy = "user")
    private List<ArticleLike> articleLikes = new ArrayList<>();

////     게시글 스크랩 매핑
//    @ManyToMany
//    @JoinTable(
//            name = "user_article_scrap",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "article_id")
//    )
//    private List<Article> articleScraps = new ArrayList<>();

    // 댓글 매핑
    @OneToMany(mappedBy = "user")
    private List<ArticleComment> articleComments = new ArrayList<>();

    // 주문 매핑
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    // 판매글 매핑
    @OneToMany(mappedBy = "users") // mappedBy user => users로 수정
    private List<Sales> salesList = new ArrayList<>();

//    // 판매글 찜하기 매핑
//    @ManyToMany
//    @JoinTable(
//            name = "user_sale_likes",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "sales_id")
//    )
//    private List<Sales> salesLikes = new ArrayList<>();

    // 판매글 찜하기 매핑
    @OneToMany(mappedBy = "user")
    private List<SalesLike> salesLikes = new ArrayList<>();

    // 이벤트 작성 매핑 => (별개)
    @OneToMany(mappedBy = "user")
    private List<Event> events = new ArrayList<>();

    // 이벤트 댓글 매핑
    @OneToMany(mappedBy = "user")
    private List<Event> eventComments = new ArrayList<>();

    // 이벤트 리뷰 매핑
    @OneToMany(mappedBy = "user")
    private List<EventReview> eventReviews = new ArrayList<>();

//    // 유저-이벤트 참가하기 매핑
//    @ManyToMany
//    @JoinTable(
//            name = "user_event_participation",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id")
//    )
//    private List<Event> eventJoinList = new ArrayList<>();
//
//    // 유저-이벤트 좋아요 매핑
//    @ManyToMany
//    @JoinTable(
//            name = "user_event_likes",
//            joinColumns = @JoinColumn(name = "user_id"),
//            inverseJoinColumns = @JoinColumn(name = "event_id")
//    )
//    private List<Event> eventLikes = new ArrayList<>();

    // 이벤트 참가하기 매핑
    @OneToMany(mappedBy = "user")
    private List<EventParticipation> eventParticipationList = new ArrayList<>();

    // 이벤트 좋아요 매핑
    @OneToMany(mappedBy = "user")
    private List<EventLike> eventLikes = new ArrayList<>();


    // 유저-아티스트 구독 매핑
    @OneToMany(mappedBy = "user")
    private List<ArtistSubscription> artistSubscriptionList = new ArrayList<>();

    // 유저-팀 구독 매핑
    @OneToMany(mappedBy = "user")
    private List<TeamSubscription> teamSubscriptionList = new ArrayList<>();

    // 채팅방 매핑
    @OneToMany(mappedBy = "sender")
    private List<ChatRoom> sendChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "receiver")
    private List<ChatRoom> receiveChatRooms = new ArrayList<>();

    // 채팅메세지 매핑
    @OneToMany(mappedBy = "sender")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public void setLastLoginAt(LocalDateTime date) {
        this.lastLoginAt = date;
    }

    public void update(User userUpdate, PasswordEncoder passwordEncoder) {
        if (userUpdate.getPassword() != null && !userUpdate.getPassword().isEmpty())
            this.password = passwordEncoder.encode(userUpdate.password);

        if (userUpdate.getNickname() != null && !userUpdate.getNickname().isEmpty())
            this.nickname = userUpdate.nickname;

        if (userUpdate.getPhone() != null && !userUpdate.getPhone().isEmpty())
            this.phone = userUpdate.phone;
    }
    public void setTermsAt(LocalDateTime date) {
        this.lastLoginAt = date;
    }

    public void setIsTermsAgreed() {
        this.isTermsAgreed = true;
    }

    public void setPassword(String password, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}

