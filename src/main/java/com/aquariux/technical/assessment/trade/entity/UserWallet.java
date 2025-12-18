package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserWallet {
    private Long id;
    private Long userId;
    private Long symbolId;
    private BigDecimal balance;
    private LocalDateTime updatedAt;

    public UserWallet(Long userId, Long symbolId, BigDecimal balance) {
        this.userId = userId;
        this.symbolId = symbolId;
        this.balance = balance;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateBalance(BigDecimal newBalance) {
        this.balance = newBalance;
        this.updatedAt = LocalDateTime.now();
    }
}