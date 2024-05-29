package com.lazure.partola.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Data;


import java.math.BigDecimal;

/**
 * @author Ivan Partola
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CurrencyPriceResponseDto {

    @JsonProperty("data")
    private Data data;

    @lombok.Data
    public static class Data {
        @JsonProperty("SOL")
        private Solana solana;

    }

    @lombok.Data
    public static class Solana {
        @JsonProperty("quote")
        private Quote quote;

    }

    @lombok.Data
    public static class Quote {
        @JsonProperty("USDC")
        private USDC usdc;
    }

    @lombok.Data
    public static class USDC {
        @JsonProperty("price")
        private BigDecimal price;
    }
}
