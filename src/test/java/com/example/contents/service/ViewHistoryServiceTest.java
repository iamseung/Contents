package com.example.contents.service;

import com.example.contents.domain.dto.CursorPaginationResponse;
import com.example.contents.domain.dto.ViewHistoryResponse;
import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.ContentRedisEntity;
import com.example.contents.domain.entity.User;
import com.example.contents.domain.entity.ViewHistory;
import com.example.contents.repository.ViewHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 조회 이력")
class ViewHistoryServiceTest {

    @InjectMocks private ViewHistoryService sut;
    @Mock private ViewHistoryRepository viewHistoryRepository;
    @Mock private ContentCacheService contentCacheService;

    @DisplayName("작품을 조회하면 이력이 저장된다")
    @Test
    void givenUserAndContent_whenViewContent_thenSaveHistory() {
        // Given
        User user = User.of("user1", "pw", true);
        Content content = mock(Content.class);
        ViewHistory mockHistory = ViewHistory.of(user, content);

        given(viewHistoryRepository.save(any(ViewHistory.class))).willReturn(mockHistory);

        // When
        ViewHistory result = sut.viewContent(user, content);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getContent()).isEqualTo(content);
        then(viewHistoryRepository).should().save(any(ViewHistory.class));
    }

    @DisplayName("작품 ID와 커서 ID로 조회 이력을 페이지 단위로 조회할 수 있다")
    @Test
    void givenContentIdAndCursor_whenGetViewHistories_thenReturnsResponse() {
        // Given
        Long contentId = 1L;
        Long cursorId = 10L;
        int size = 2;

        ContentRedisEntity contentRedisEntity = new ContentRedisEntity(contentId, "title", "desc", false, true, null, null, null);

        User u1 = User.of("user1", "pw", true);
        User u2 = User.of("user2", "pw", true);
        Content dummyContent = Content.of("title", "desc", false, true);

        ViewHistory h1 = ViewHistory.of(u1, dummyContent);
        ViewHistory h2 = ViewHistory.of(u2, dummyContent);

        given(contentCacheService.getContentLocalCache(contentId)).willReturn(contentRedisEntity);
        given(viewHistoryRepository.findByContentIdWithCursor(eq(contentId), eq(cursorId), any(Pageable.class)))
                .willReturn(List.of(h1, h2));

        // When
        CursorPaginationResponse<ViewHistoryResponse> result = sut.getViewHistories(contentId, cursorId, size);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getNextCursorId()).isEqualTo(result.getItems().get(1).getId());
        then(contentCacheService).should().getContentLocalCache(contentId);
        then(viewHistoryRepository).should().findByContentIdWithCursor(eq(contentId), eq(cursorId), any(Pageable.class));
    }

    @DisplayName("조회 이력 ID로 삭제할 수 있다")
    @Test
    void givenId_whenDelete_thenRemoveEntity() {
        // Given
        Long viewHistoryId = 1L;

        // When
        sut.deleteViewHistory(viewHistoryId);

        // Then
        then(viewHistoryRepository).should().deleteById(viewHistoryId);
    }

    @DisplayName("작품 ID로 모든 조회 이력을 삭제할 수 있다")
    @Test
    void givenContentId_whenDeleteAllByContentId_thenRemoveAll() {
        // Given
        Long contentId = 10L;

        // When
        sut.deleteAllByContentId(contentId);

        // Then
        then(viewHistoryRepository).should().deleteAllByContentId(contentId);
    }
}