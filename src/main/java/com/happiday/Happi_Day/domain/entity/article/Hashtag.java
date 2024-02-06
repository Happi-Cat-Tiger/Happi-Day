package com.happiday.Happi_Day.domain.entity.article;

import com.happiday.Happi_Day.domain.entity.event.Event;
import com.happiday.Happi_Day.domain.entity.product.SalesHashtag;
import com.happiday.Happi_Day.domain.entity.event.EventHashtag;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name ="hashtag")
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    private String tag;

//    @ManyToMany(mappedBy ="hashtags")
//    private List<Article> articles = new ArrayList<>();
    @OneToMany(mappedBy = "hashtag")
    private List<ArticleHashtag> articleHashtags = new ArrayList<>();

//    @ManyToMany(mappedBy = "hashtags")
//    private List<Sales> sales = new ArrayList<>();
    @OneToMany(mappedBy = "hashtag")
    private List<SalesHashtag> salesHashtags = new ArrayList<>();

    @OneToMany(mappedBy = "hashtag")
    private List<EventHashtag> eventHashtags = new ArrayList<>();
}
