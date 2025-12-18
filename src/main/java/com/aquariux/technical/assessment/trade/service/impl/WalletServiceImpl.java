package com.aquariux.technical.assessment.trade.service.impl;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.dto.response.WalletBalanceResponse;
import com.aquariux.technical.assessment.trade.entity.UserWallet;
import com.aquariux.technical.assessment.trade.mapper.UserWalletMapper;
import com.aquariux.technical.assessment.trade.service.WalletServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletServiceInterface {

    private final UserWalletMapper userWalletMapper;

    public List<WalletBalanceResponse> getUserWalletBalances(Long userId) {
        List<UserWalletDto> wallets = userWalletMapper.findByUserId(userId);
        
        return wallets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Map<Long, UserWalletDto> getUserWalletBalanceMap(Long userId) {
        List<UserWalletDto> wallets = userWalletMapper.findByUserId(userId);

        return wallets.stream()
                .collect(Collectors.toMap(
                        UserWalletDto::getSymbolId,
                        Function.identity(),
                        (oldValue, newValue) -> newValue
                ));
    }

    private WalletBalanceResponse mapToResponse(UserWalletDto wallet) {
        WalletBalanceResponse response = new WalletBalanceResponse();
        response.setSymbol(wallet.getSymbol());
        response.setName(wallet.getName());
        response.setBalance(wallet.getBalance());
        return response;
    }

    public UserWallet getUserWalletByUserAndSymbol(Long userId, Long symbolId) {
        return userWalletMapper.findUserWallet(userId, symbolId);
    }

    public void updateExistingElseCreate(Long userId, Long symbolId, BigDecimal newBalance) {
        UserWallet userWalletTargetSymbol = getUserWalletByUserAndSymbol(userId, symbolId);
        if (newBalance == null) {
            // for now set to zero first if not provided
            newBalance = java.math.BigDecimal.ZERO;
        }
        if (userWalletTargetSymbol == null) {
            // create new user wallet
            userWalletTargetSymbol = new UserWallet(userId, symbolId, newBalance);
        } else {
            userWalletTargetSymbol.updateBalance(newBalance);
        }
        upsert(userWalletTargetSymbol);

    }

    public void updateExisting(UserWallet userWallet, BigDecimal newBalance) {
        userWallet.updateBalance(newBalance);
        upsert(userWallet);
    }

    private void upsert(UserWallet userWallet) {
        int updated = userWalletMapper.updateByUserIdSymbolId(userWallet);
        if (updated == 0) {
            userWalletMapper.insert(userWallet);
        }
    }
}