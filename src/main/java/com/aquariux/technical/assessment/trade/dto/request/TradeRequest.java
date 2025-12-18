package com.aquariux.technical.assessment.trade.dto.request;

import com.aquariux.technical.assessment.trade.enums.TradeType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradeRequest {
    @NotBlank
    private Long userId;
    @NotBlank
    private TradeType tradeType;
    @NotBlank
    private BigDecimal quantity;
    @NotBlank
    private String targetSymbol;
    
    // TODO: What information do you need to execute a trade?
}