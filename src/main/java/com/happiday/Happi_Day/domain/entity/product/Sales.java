package com.happiday.Happi_Day.domain.entity.product;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.artist.ArtistSales;
import com.happiday.Happi_Day.domain.entity.team.TeamSales;
import com.happiday.Happi_Day.domain.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "sales")
@SQLDelete(sql = "UPDATE sales SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Sales extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 판매글 카테고리 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private SalesCategory salesCategory;

    // 판매자 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SalesStatus salesStatus;

    @Setter
    private String thumbnailImage;

    @Setter
    @ElementCollection
    private List<String> imageUrl = new ArrayList<>();

    @Column(nullable = false)
    private String account;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    // 상품
    @OneToMany(mappedBy = "sales")
    private List<Product> products = new ArrayList<>();

    // 배송방법
    @OneToMany(mappedBy = "sales")
    private List<Delivery> deliveries = new ArrayList<>();

    // 주문 매핑
    @OneToMany(mappedBy = "sales")
    private List<Order> orders = new ArrayList<>();

    // 판매글 찜하기
    @OneToMany(mappedBy = "sales")
    private List<SalesLike> salesLikes = new ArrayList<>();

    // 아티스트-판매글 매핑
    @OneToMany(mappedBy = "sales")
    private List<ArtistSales> artistSalesList = new ArrayList<>();

    // 팀-판매글 매핑
    @OneToMany(mappedBy = "sales")
    private List<TeamSales> teamSalesList = new ArrayList<>();

    // 해시태그 매핑
    @Setter
    @OneToMany(mappedBy = "sales")
    private List<SalesHashtag> salesHashtags = new ArrayList<>();

    public void updateStatus(SalesStatus status) {
        this.salesStatus = status;
    }

    public void updateSales(Sales sales) {
        if (sales.getName() != null) this.name = sales.getName();
        if (sales.getDescription() != null) this.description = sales.getDescription();
        if (sales.getSalesStatus() != null) this.salesStatus = sales.getSalesStatus();
        if (sales.getArtistSalesList() != null) this.artistSalesList = sales.getArtistSalesList();
        if (sales.getTeamSalesList() != null) this.teamSalesList = sales.getTeamSalesList();
        if (sales.getAccount() != null) this.account = sales.getAccount();
    }
}
