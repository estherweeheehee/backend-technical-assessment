package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.response.BestPriceResponse;
import com.aquariux.technical.assessment.trade.entity.CryptoPrice;
import com.aquariux.technical.assessment.trade.mapper.CryptoPriceMapper;
import com.aquariux.technical.assessment.trade.service.PriceServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceServiceInterface {

    private final CryptoPriceMapper cryptoPriceMapper;

    @Override
    public List<BestPriceResponse> getLatestBestPrices() {
        List<CryptoPrice> latestPrices = cryptoPriceMapper.findLatestPrices();
        
        return latestPrices.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BestPriceResponse getLatestBestPriceByPairId(Long pairId) {
        CryptoPrice latestPrice = cryptoPriceMapper.findLatestPriceByPairId(pairId)
                .orElseThrow(() -> new RuntimeException("Unable to get latest price by pairId: " + pairId));

        return mapToResponse(latestPrice);
    }

    private BestPriceResponse mapToResponse(CryptoPrice price) {
        BestPriceResponse response = new BestPriceResponse();
        response.setPairName(price.getPairName());
        response.setBidPrice(price.getBidPrice());
        response.setAskPrice(price.getAskPrice());
        response.setBidSource(price.getBidSource());
        response.setAskSource(price.getAskSource());
        response.setTimestamp(price.getCreatedAt());
        return response;
    }
}