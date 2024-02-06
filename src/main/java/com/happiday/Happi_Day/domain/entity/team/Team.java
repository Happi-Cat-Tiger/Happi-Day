package com.happiday.Happi_Day.domain.entity.team;

import com.happiday.Happi_Day.domain.entity.BaseEntity;
import com.happiday.Happi_Day.domain.entity.artist.Artist;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder(toBuilder = true)
@SQLDelete(sql="UPDATE team SET deleted_at = now() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    private String logoUrl;

    @ManyToMany(mappedBy = "teams")
    private List<Artist> artists = new ArrayList<>();

    // 이벤트
    @OneToMany(mappedBy = "team")
    private List<TeamEvent> events = new ArrayList<>();

    // 판매글
    @ManyToMany(mappedBy = "teams")
    private List<Sales> salesList = new ArrayList<>();

    // 유저 구독
    @OneToMany(mappedBy = "team")
    private List<TeamSubscription> subscribers = new ArrayList<>();

    // 게시판
    @OneToMany(mappedBy = "team")
    private List<TeamArticle> teamArticleList = new ArrayList<>();

    public void update(Team teamUpdate) {
        this.name  = teamUpdate.getName();
        this.description = teamUpdate.getDescription();
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
}
