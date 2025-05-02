package com.example.contents.repository;

import com.example.contents.domain.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {
}
