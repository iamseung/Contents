package com.example.contents.service;

import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.PurchaseHistory;
import com.example.contents.domain.entity.User;
import com.example.contents.repository.PurchaseHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 구매 이력")
class PurchaseHistoryServiceTest {

    @InjectMocks private PurchaseHistoryService sut;
    @Mock private PurchaseHistoryRepository purchaseHistoryRepository;

    @DisplayName("작품을 구매하면 구매 이력이 저장된다")
    @Test
    void givenUserAndContent_whenCreatePurchaseHistory_thenSaveHistory() {
        // Given
        User user = User.of("user1", "pw", true);
        Content content = mock(Content.class);
        PurchaseHistory mockHistory = PurchaseHistory.of(user, content);

        given(purchaseHistoryRepository.save(any(PurchaseHistory.class))).willReturn(mockHistory);

        // When
        PurchaseHistory result = sut.createPurchaseHistory(user, content);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getContent()).isEqualTo(content);
        then(purchaseHistoryRepository).should().save(any(PurchaseHistory.class));
    }

    @DisplayName("구매 이력 ID로 삭제할 수 있다")
    @Test
    void givenId_whenDelete_thenRemoveEntity() {
        // Given
        Long historyId = 1L;

        // When
        sut.deletePurchaseHistory(historyId);

        // Then
        then(purchaseHistoryRepository).should().deleteById(historyId);
    }

    @DisplayName("작품 ID로 모든 구매 이력을 삭제할 수 있다")
    @Test
    void givenContentId_whenDeleteAllByContentId_thenRemoveAll() {
        // Given
        Long contentId = 10L;

        // When
        sut.deleteAllByContentId(contentId);

        // Then
        then(purchaseHistoryRepository).should().deleteAllByContentId(contentId);
    }
}