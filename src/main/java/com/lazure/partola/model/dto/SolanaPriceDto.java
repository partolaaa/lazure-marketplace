package com.lazure.partola.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Ivan Partola
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolanaPriceDto {
    private BigDecimal price;
}
