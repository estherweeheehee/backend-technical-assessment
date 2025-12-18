package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.request.TradeRequest;
import com.aquariux.technical.assessment.trade.dto.response.BestPriceResponse;
import com.aquariux.technical.assessment.trade.dto.response.TradeResponse;
import com.aquariux.technical.assessment.trade.entity.CryptoPair;
import com.aquariux.technical.assessment.trade.entity.Symbol;
import com.aquariux.technical.assessment.trade.entity.Trade;
import com.aquariux.technical.assessment.trade.entity.UserWallet;
import com.aquariux.technical.assessment.trade.enums.TradeType;
import com.aquariux.technical.assessment.trade.mapper.CryptoPairMapper;
import com.aquariux.technical.assessment.trade.mapper.SymbolMapper;
import com.aquariux.technical.assessment.trade.mapper.TradeMapper;
import com.aquariux.technical.assessment.trade.service.PriceServiceInterface;
import com.aquariux.technical.assessment.trade.service.TradeServiceInterface;
import com.aquariux.technical.assessment.trade.service.WalletServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeServiceInterface {

    private final TradeMapper tradeMapper;
    private final SymbolMapper symbolMapper;
    private final CryptoPairMapper cryptoPairMapper;
    private final PriceServiceInterface priceService;
    private final WalletServiceInterface walletService;
    // Add additional beans here if needed for your implementation

    @Override
    @Transactional
    public TradeResponse executeTrade(TradeRequest tradeRequest) {
        // TODO: Implement the core trading engine
        // What should happen when a user executes a trade?

        // assuming user validation done, ensure user exists and is verified

        TradeType tradeType = tradeRequest.getTradeType();
        BigDecimal quantity = tradeRequest.getQuantity();
        String targetSymbol = tradeRequest.getTargetSymbol();
        Long userId = tradeRequest.getUserId();

        try {
            // validate crypto pair
            CryptoPair cryptoPair = verifyAndGetCryptoPair(targetSymbol);
            if (!cryptoPair.getActive()) {
                throw new RuntimeException("crypto pair is inactive");
            }
            // get best price based on crypto pair
            BestPriceResponse bestPrice = priceService.getLatestBestPriceByPairId(cryptoPair.getId());
            BigDecimal price;

            if (tradeType == TradeType.BUY) {
                price = bestPrice.getAskPrice();
                buyTrade(userId, quantity, cryptoPair, price);

            } else {
                price = bestPrice.getBidPrice();
                sellTrade(userId, quantity, cryptoPair, price);
            }

            Trade trade = logTrade(userId, cryptoPair.getId(), tradeType.toString(), quantity, price);
            return new TradeResponse(trade, cryptoPair);

        } catch (Exception e) {
            throw new RuntimeException("Unable to trade - " + e.getLocalizedMessage());
        }

    }

    private Trade logTrade(Long userId, Long cryptoPairId, String tradeType, BigDecimal quantity, BigDecimal price) {
        BigDecimal totalAmount = quantity.multiply(price);
        Trade trade =  new Trade(userId, cryptoPairId, tradeType, quantity, price, totalAmount);
        int result = tradeMapper.insert(trade);
        if (result == 0) {
            throw new RuntimeException("Unable to save new trade");
        }
        return trade;
    }

    private void sellTrade(Long userId, BigDecimal quantity, CryptoPair cryptoPair, BigDecimal bidPrice) {
        // check if wallet got sufficient sellingCurrency
        UserWallet userWalletSellingCurrency = walletService.getUserWalletByUserAndSymbol(userId, cryptoPair.getBaseSymbolId());
        if (userWalletSellingCurrency == null) {
            throw new RuntimeException("Unable to sell as User does not have any: " + cryptoPair.getBaseSymbolId());
        }

        BigDecimal sellingCurrencyBalance = userWalletSellingCurrency.getBalance();
        // since selling targetCurrency to USDT to check if enough sellingCurrency to fill ask price x quantity
        if (quantity.compareTo(sellingCurrencyBalance) > 0) {
            throw new RuntimeException("Unable to sell as insufficient target balance");
        }

        // Update balances
        // update target balance
        BigDecimal newTargetBalance = sellingCurrencyBalance.subtract(quantity);
        walletService.updateExisting(userWalletSellingCurrency, newTargetBalance);

        // if user dont have the USDT balance, create it
        BigDecimal totalSellAmount = quantity.multiply(bidPrice);
        walletService.updateExistingElseCreate(userId, cryptoPair.getQuoteSymbolId(), totalSellAmount);

    }

    private void buyTrade(Long userId, BigDecimal quantity, CryptoPair cryptoPair, BigDecimal askPrice) {
        // check if wallet got sufficient USDT
        UserWallet userWalletUSDT = walletService.getUserWalletByUserAndSymbol(userId, cryptoPair.getQuoteSymbolId());
        if (userWalletUSDT == null) {
            throw new RuntimeException("User does not have USDT, unable to buy");
        }

        BigDecimal USDTBalance = userWalletUSDT.getBalance();
        // since buying targetCurrency using USDT, to check if enough USDT to fill ask price x quantity
        BigDecimal totalBuyAmount = quantity.multiply(askPrice);
        if (totalBuyAmount.compareTo(USDTBalance) > 0) {
            throw new RuntimeException("Insufficient USDT Balance");
        }

        // Update balances
        // update USDT balance
        BigDecimal newUSDTBalance = USDTBalance.subtract(totalBuyAmount);
        walletService.updateExisting(userWalletUSDT, newUSDTBalance);

        // if user dont have the new currency's balance, create it
        walletService.updateExistingElseCreate(userId, cryptoPair.getBaseSymbolId(), quantity);
    }

    private CryptoPair verifyAndGetCryptoPair(String targetSymbol) {
        Symbol targetSymbolEntity = symbolMapper.findBySymbol(targetSymbol)
                .orElseThrow(() -> new RuntimeException("Unable to find target symbol under: " + targetSymbol));

        if (!targetSymbolEntity.getActive()) {
            throw new RuntimeException("Target symbol is inactive");
        }
        return cryptoPairMapper.findIdByBaseSymbolId(targetSymbolEntity.getId())
                .orElseThrow(() -> new RuntimeException("Unable to find crypto pair"));

    }
}