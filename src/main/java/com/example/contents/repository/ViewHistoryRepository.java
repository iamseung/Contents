package com.example.contents.repository;

import com.example.contents.domain.dto.PopularViewContentResponse;
import com.example.contents.domain.entity.ViewHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    @Query("""
    SELECT v FROM ViewHistory v
    JOIN FETCH v.user
    WHERE v.content.id = :contentId
    AND (:cursorId IS NULL OR v.id < :cursorId)
    ORDER BY v.id DESC
""")
    List<ViewHistory> findByContentIdWithCursor(
            @Param("contentId") Long contentId,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

    @Query("""
        SELECT new com.example.contents.domain.dto.PopularViewContentResponse(
            vh.content.id,
            vh.content.title,
            COUNT(vh.id)
        )
        FROM ViewHistory vh
        GROUP BY vh.content.id, vh.content.title
        ORDER BY COUNT(vh.id) DESC
        """)
    List<PopularViewContentResponse> findTop10ByOrderByViewCountDesc();

    @Modifying
    @Query("DELETE FROM ViewHistory v WHERE v.content.id = :contentId")
    void deleteAllByContentId(@Param("contentId") Long contentId);
}
