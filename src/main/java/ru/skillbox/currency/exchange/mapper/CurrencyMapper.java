package ru.skillbox.currency.exchange.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.currency.exchange.dto.CurrencyDto;
import ru.skillbox.currency.exchange.dto.ShortCurrencyDto;
import ru.skillbox.currency.exchange.entity.Currency;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyDto convertToDto(Currency currency);

    @Mapping(target = "value",
            expression = "java(currency.getValue().divide(new java.math.BigDecimal(currency.getNominal()),8, " +
                    "java.math.RoundingMode.HALF_EVEN).stripTrailingZeros().toPlainString())")
    ShortCurrencyDto convertToShortDto(Currency currency);

    Currency convertToEntity(CurrencyDto currencyDto);
}
