package com.example.contents.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "purchase_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Content content;

    private LocalDateTime purchasedAt;

    private PurchaseHistory(User user, Content content) {
        this.user = user;
        this.content = content;
        this.purchasedAt = LocalDateTime.now();
    }

    public static PurchaseHistory of(User user, Content content) {
        return new PurchaseHistory(user, content);
    }
}