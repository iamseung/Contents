package com.example.contents.service;

import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.PurchaseHistory;
import com.example.contents.domain.entity.User;
import com.example.contents.repository.PurchaseHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Transactional
    public PurchaseHistory createPurchaseHistory(User user, Content content) {
        return purchaseHistoryRepository.save(PurchaseHistory.of(user, content));
    }

    // 단일 삭제
    @Transactional
    public void deletePurchaseHistory(Long purchaseHistoryId) {
        purchaseHistoryRepository.deleteById(purchaseHistoryId);
    }

    // 전체 삭제, contentId
    @Transactional
    public void deleteAllByContentId(Long contentId) {
        purchaseHistoryRepository.deleteAllByContentId(contentId);
    }
}
