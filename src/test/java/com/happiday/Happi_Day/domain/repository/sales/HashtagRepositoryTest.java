package com.happiday.Happi_Day.domain.repository.sales;

import com.happiday.Happi_Day.domain.entity.article.Hashtag;
import com.happiday.Happi_Day.domain.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class HashtagRepositoryTest {
    @Autowired
    HashtagRepository hashtagRepository;

    @BeforeEach
    void beforeEach() {
        Hashtag newHashtag = Hashtag.builder()
                .tag("test tagName")
                .build();
        hashtagRepository.save(newHashtag);
    }

    @Test
    @DisplayName("태그이름으로 해시태그 조회")
    public void findByTagTest() {
        // given
        String tagName = "test tagName";

        // when
        Optional<Hashtag> foundHashtag = hashtagRepository.findByTag(tagName);

        // then
        assertThat(foundHashtag.get()).isNotNull();
        assertThat(foundHashtag.get().getTag()).isEqualTo(tagName);
    }

}
