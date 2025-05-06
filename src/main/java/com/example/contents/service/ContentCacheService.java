package com.example.contents.service;

import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.ContentRedisEntity;
import com.example.contents.exception.BaseException;
import com.example.contents.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import static com.example.contents.exception.ErrorCode.NOT_EXIST_CONTENT;

@Service
@RequiredArgsConstructor
public class ContentCacheService {

    private final ContentRepository contentRepository;

    private ContentCacheService proxy() {
        return ((ContentCacheService) AopContext.currentProxy());
    }

    // 캐싱 30분
    @Cacheable(cacheNames = "content", key = "#contentId")
    public ContentRedisEntity getContentCache(Long contentId) {
        Content content = contentRepository.findById(contentId).orElseThrow(
                () -> new BaseException(NOT_EXIST_CONTENT, "존재하지 않는 작품입니다. contentId : %s".formatted(contentId))
        );

        return new ContentRedisEntity(content);
    }

    @Cacheable(cacheNames = "content", key = "#contentId", cacheManager = "localCacheManager")
    public ContentRedisEntity getContentLocalCache(Long contentId) {
        return proxy().getContentCache(contentId);
    }

    @CacheEvict(cacheNames = "content", key = "#contentId")
    public void evictContentCache(Long contentId) {}

    @CacheEvict(cacheNames = "content", key = "#contentId", cacheManager = "localCacheManager")
    public void evictContentLocalCache(Long contentId) {}

    // Content 의 무료/유료, 이벤트 기간 수정 시 초기화를 위한 메서드
    @CachePut(cacheNames = "content")
    public ContentRedisEntity putContentCache(Long contentId) {
        return proxy().getContentCache(contentId);
    }

    // Content 의 무료/유료, 이벤트 기간 수정 시 초기화를 위한 메서드
    @CachePut(cacheNames = "content")
    public ContentRedisEntity putContentLocalCache(Long contentId) {
        return proxy().getContentLocalCache(contentId);
    }
}
