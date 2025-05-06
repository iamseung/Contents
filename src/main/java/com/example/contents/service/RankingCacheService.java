package com.example.contents.service;

import com.example.contents.domain.dto.PopularPurchaseContentResponse;
import com.example.contents.domain.dto.PopularViewContentResponse;
import com.example.contents.repository.PurchaseHistoryRepository;
import com.example.contents.repository.ViewHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingCacheService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final PurchaseHistoryRepository purchaseHistoryRepository;

    private RankingCacheService proxy() {
        return ((RankingCacheService) AopContext.currentProxy());
    }

    // 2. 인기 작품 조회, 가장 많이 조회한 작품 상위 10개를 조회
    @Cacheable(cacheNames = "MostPopularPurchaseContentsTop10")
    public List<PopularPurchaseContentResponse> getMostPopularPurchaseContentsTop10Cache() {
        return purchaseHistoryRepository.findTop10ByOrderByPurchaseCountDesc();
    }

    @Cacheable(cacheNames = "MostPopularPurchaseContentsTop10", cacheManager = "localCacheManager")
    public List<PopularPurchaseContentResponse> getMostPopularPurchaseContentsTop10LocalCache() {
        return proxy().getMostPopularPurchaseContentsTop10Cache();
    }

    // 4. 구매 인기 작품 조회, 가장 많이 구매한 작품 상위 10개
    @Cacheable(cacheNames = "MostPopularContentsTop10")
    public List<PopularViewContentResponse> getMostPopularContentTop10Cache() {
        return viewHistoryRepository.findTop10ByOrderByViewCountDesc();
    }

    @Cacheable(cacheNames = "MostPopularPurchaseContentsTop10", cacheManager = "localCacheManager")
    public List<PopularViewContentResponse> getMostPopularContentTop10LocalCache() {
        return proxy().getMostPopularContentTop10Cache();
    }
}
