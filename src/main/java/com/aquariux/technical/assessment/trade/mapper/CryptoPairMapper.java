package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.entity.CryptoPair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface CryptoPairMapper {
    
    @Select("""
            SELECT id FROM crypto_pairs WHERE pair_name = #{pairName}
            """)
    Long findIdByPairName(String pairName);

    @Select("""
            SELECT * FROM crypto_pairs WHERE base_symbol_id = #{baseSymbolId}
            """)
    Optional<CryptoPair> findIdByBaseSymbolId(Long baseSymbolId);
}