package com.happiday.Happi_Day.domain.entity.product;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "sales_hashtag")
public class SalesHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 판매글 id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sales_id")
    private Sales sales;

    // 해시태그 id
    @ManyToOne(fetch =  FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;
}
