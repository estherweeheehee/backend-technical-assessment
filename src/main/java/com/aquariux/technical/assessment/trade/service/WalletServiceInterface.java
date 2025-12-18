package com.aquariux.technical.assessment.trade.service;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.entity.UserWallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface WalletServiceInterface {
    List<WalletBalanceResponse> getUserWalletBalances(Long userId);
    Map<Long, UserWalletDto> getUserWalletBalanceMap(Long userId);
    UserWallet getUserWalletByUserAndSymbol(Long userId, Long symbolId);
    void updateExisting(UserWallet userWallet, BigDecimal newBalance);
    void updateExistingElseCreate(Long userId, Long symbolId, BigDecimal newBalance);
}