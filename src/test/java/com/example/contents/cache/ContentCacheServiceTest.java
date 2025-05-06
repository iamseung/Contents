package com.example.contents.cache;

import static org.junit.jupiter.api.Assertions.*;

import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.ContentRedisEntity;
import com.example.contents.repository.ContentRepository;
import com.example.contents.service.ContentCacheService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@SpringBootTest
@DisplayName("캐시 테스트 - ContentCacheService")
class ContentCacheServiceTest {

    @Autowired private ContentCacheService contentCacheService;
    @MockBean private ContentRepository contentRepository;
    @Autowired private CacheManager cacheManager;

    @DisplayName("Content 캐싱이 정상 동작하면 두 번째 호출에서는 DB를 조회하지 않는다")
    @Test
    void givenContentId_whenGetContentCacheTwice_thenSecondUsesCache() {
        // Given
        Long contentId = 1L;
        Content content = mock(Content.class);
        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));

        // When - 1차 호출 (DB 접근)
        ContentRedisEntity firstCall = contentCacheService.getContentCache(contentId);
        // When - 2차 호출 (캐시 사용)
        ContentRedisEntity secondCall = contentCacheService.getContentCache(contentId);

        // Then
        assertThat(firstCall).isNotNull();
        assertThat(secondCall).isNotNull();
        assertThat(firstCall).usingRecursiveComparison().isEqualTo(secondCall);

        // verify DB only called once
        then(contentRepository).should(times(1)).findById(contentId);
    }

    @DisplayName("캐시에 저장된 값은 직접 CacheManager로도 조회할 수 있다")
    @Test
    void givenContentCache_whenManuallyCheckingCacheManager_thenValueExists() {
        // Given
        Long contentId = 2L;
        Content content = mock(Content.class);
        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));

        // When
        ContentRedisEntity cached = contentCacheService.getContentCache(contentId);

        // Then
        ContentRedisEntity fromCache = cacheManager
                .getCache("content")
                .get(contentId, ContentRedisEntity.class);

        assertThat(fromCache).isNotNull();
        assertThat(fromCache).usingRecursiveComparison().isEqualTo(cached);
    }
}