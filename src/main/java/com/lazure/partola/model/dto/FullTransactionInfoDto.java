package com.lazure.partola.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Ivan Partola
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullTransactionInfoDto {
    private String buyerWalletId;
    private String sellerWalletId;
    LocalDateTime createdTime;
    private String txId;
    private ProductDto productDto;
}
