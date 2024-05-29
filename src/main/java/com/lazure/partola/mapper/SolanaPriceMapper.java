package com.lazure.partola.mapper;

import com.lazure.partola.model.dto.CurrencyPriceResponseDto;
import com.lazure.partola.model.dto.SolanaPriceDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

/**
 * @author Ivan Partola
 */
@Mapper(componentModel = SPRING)
public abstract class SolanaPriceMapper {
    @Mapping(target = "price", source = "data.solana.quote.usdc.price")
    public abstract SolanaPriceDto toSolanaPriceDto(CurrencyPriceResponseDto currencyPriceResponseDto);
}
