package com.example.contents.service;

import com.example.contents.domain.dto.UserDto;
import com.example.contents.domain.entity.Content;
import com.example.contents.domain.entity.User;
import com.example.contents.exception.BaseException;
import com.example.contents.repository.ContentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.example.contents.exception.ErrorCode.CANT_BUY_CONTENT;
import static com.example.contents.exception.ErrorCode.NOT_EXIST_CONTENT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("비즈니스 로직 - 작품")
class ContentServiceTest {

    @InjectMocks private ContentService sut;

    @Mock private ContentRepository contentRepository;
    @Mock private ViewHistoryService viewHistoryService;
    @Mock private PurchaseHistoryService purchaseHistoryService;
    @Mock private UserService userService;

    @DisplayName("존재하지 않는 ID로 작품 조회 시 예외 발생")
    @Test
    void givenWrongId_whenFindContent_thenThrowsException() {
        // Given
        Long notFoundContentId = 100L;
        given(contentRepository.findById(notFoundContentId)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> sut.findContentById(notFoundContentId))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", NOT_EXIST_CONTENT);
    }

    @DisplayName("성인 제한 작품을 비성인 유저가 조회 시 예외 발생")
    @Test
    void givenAdultContent_whenNonAdultUserViews_thenThrowsException() {
        // Given
        Long contentId = 1L;
        UserDto userDto = UserDto.of("user1", "pw", false);
        User user = User.of("user1", "pw", false);
        Content content = mock(Content.class);

        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
        given(userService.findUserById(any())).willReturn(user);
        given(content.isCanAccess(user)).willReturn(true);

        // When & Then
        assertThatThrownBy(() -> sut.viewContent(contentId, userDto))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", CANT_BUY_CONTENT);

        then(viewHistoryService).shouldHaveNoInteractions();
    }

    @DisplayName("성인 유저가 작품을 조회하면 ViewHistory 가 기록된다")
    @Test
    void givenValidAccess_whenViewingContent_thenRecordViewHistory() {
        // Given
        Long contentId = 1L;
        UserDto userDto = UserDto.of("user1", "pw", true);
        User user = User.of("user1", "pw", true);
        Content content = mock(Content.class);

        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
        given(userService.findUserById(any())).willReturn(user);
        given(content.isCanAccess(user)).willReturn(false); // 접근 가능

        // When
        sut.viewContent(contentId, userDto);

        // Then
        then(viewHistoryService).should().viewContent(user, content);
    }

    @DisplayName("유료 작품일 경우 구매 불가 예외 발생")
    @Test
    void givenPaidContent_whenPurchase_thenThrowsException() {
        // Given
        Long contentId = 1L;
        UserDto userDto = UserDto.of("user1", "pw", true);
        User user = User.of("user1", "pw", true);
        Content content = mock(Content.class);

        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
        given(userService.findUserById(any())).willReturn(user);
        given(content.isCanAccess(user)).willReturn(true);
        given(content.isFree()).willReturn(false); // 유료

        // When & Then
        assertThatThrownBy(() -> sut.purchaseContent(contentId, userDto))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", CANT_BUY_CONTENT);

        then(purchaseHistoryService).shouldHaveNoInteractions();
    }

    @DisplayName("무료 작품은 구매 성공")
    @Test
    void givenFreeContent_whenPurchase_thenSuccess() {
        // Given
        Long contentId = 1L;
        UserDto userDto = UserDto.of("user1", "pw", true);
        User user = User.of("user1", "pw", true);
        Content content = mock(Content.class);

        given(contentRepository.findById(contentId)).willReturn(Optional.of(content));
        given(userService.findUserById(any())).willReturn(user);
        given(content.isCanAccess(user)).willReturn(true);
        given(content.isFree()).willReturn(true); // 무료

        // When
        sut.purchaseContent(contentId, userDto);

        // Then
        then(purchaseHistoryService).should().createPurchaseHistory(user, content);
    }

    @DisplayName("작품 삭제 시 연관 이력도 삭제된다")
    @Test
    void givenContentId_whenDelete_thenCascadeDelete() {
        // Given
        Long contentId = 1L;

        // When
        sut.deleteContent(contentId);

        // Then
        then(viewHistoryService).should().deleteAllByContentId(contentId);
        then(purchaseHistoryService).should().deleteAllByContentId(contentId);
        then(contentRepository).should().deleteById(contentId);
    }
}