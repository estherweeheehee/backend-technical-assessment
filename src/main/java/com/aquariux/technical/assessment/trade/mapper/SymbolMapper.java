package com.aquariux.technical.assessment.trade.mapper;

import com.aquariux.technical.assessment.trade.entity.Symbol;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Optional;

@Mapper
public interface SymbolMapper {
    @Select("""
            SELECT * FROM symbols WHERE symbol = #{symbol}
            """)
    Optional<Symbol> findBySymbol(String symbol);

    @Select("""
            SELECT id FROM symbols WHERE symbol = #{symbol}
            """)
    Long findIdBySymbol(String symbol);
}
