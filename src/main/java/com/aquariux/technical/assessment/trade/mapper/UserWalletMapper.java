package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.dto.internal.UserWalletDto;
import com.aquariux.technical.assessment.trade.entity.UserWallet;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserWalletMapper {
    
    @Select("""
            SELECT s.symbol, s.name, uw.balance 
            FROM symbols s 
            INNER JOIN user_wallets uw ON s.id = uw.symbol_id AND uw.user_id = #{userId} 
            ORDER BY s.symbol
            """)
    List<UserWalletDto> findByUserId(Long userId);

    @Select("""
            SELECT *
            FROM user_wallets uw
            WHERE uw.user_id = #{userId} AND uw.symbol_id = #{symbolId}
            """)
    UserWallet findUserWallet(Long userId, Long symbolId);

    @Insert("""
        INSERT INTO user_wallets (user_id, symbol_id, balance, updated_at)
        VALUES (#{userId}, #{symbolId}, #{balance}, #{updatedAt})
      """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserWallet userWallet);

    @Update("""
    UPDATE user_wallets
    SET
        balance = #{balance},
        updated_at = #{updatedAt}
    WHERE user_id = #{userId} AND symbol_id = #{symbolId}
  """)
    int updateByUserIdSymbolId(UserWallet userWallet);
}