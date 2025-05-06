package com.example.contents.repository;

import com.example.contents.domain.dto.PopularPurchaseContentResponse;
import com.example.contents.domain.entity.PurchaseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    @Query("""
        SELECT new com.example.contents.domain.dto.PopularPurchaseContentResponse(
            ph.content.id,
            ph.content.title,
            COUNT(ph.id)
        )
        FROM PurchaseHistory ph
        GROUP BY ph.content.id, ph.content.title
        ORDER BY COUNT(ph.id) DESC
        """)
    List<PopularPurchaseContentResponse> findTop10ByOrderByPurchaseCountDesc();

    @Modifying
    @Query("DELETE FROM PurchaseHistory p WHERE p.content.id = :contentId")
    void deleteAllByContentId(@Param("contentId") Long contentId);
}
