package com.aquariux.technical.assessment.trade.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Trade {
    private Long id;
    private Long userId;
    private Long cryptoPairId;
    private String tradeType;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;
    private LocalDateTime tradeTime;

    public Trade(Long userId, Long cryptoPairId, String tradeType,
                 BigDecimal quantity, BigDecimal price,
                 BigDecimal totalAmount) {
        this.userId = userId;
        this.cryptoPairId = cryptoPairId;
        this.tradeType = tradeType;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
        this.tradeTime = LocalDateTime.now();
    }
}