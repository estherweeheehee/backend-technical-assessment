package com.aquariux.technical.assessment.trade.dto.response;

import com.aquariux.technical.assessment.trade.entity.CryptoPair;
import com.aquariux.technical.assessment.trade.entity.Trade;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeResponse {
    // TODO: What should you return after a trade is executed?
    private String tradeType;
    private BigDecimal quantity;
    private BigDecimal price;
    private LocalDateTime tradeTime;
    private String cryptoPairName;
    private Long cryptoPairId;

    public TradeResponse(Trade trade, CryptoPair cryptoPair) {
        this. tradeType = trade.getTradeType();
        this.quantity = trade.getQuantity();
        this.price = trade.getPrice();
        this.tradeTime = trade.getTradeTime();
        this.cryptoPairId = cryptoPair.getId();
        this.cryptoPairName = cryptoPair.getPairName();
    }
}