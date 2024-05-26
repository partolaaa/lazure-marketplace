package com.lazure.partola.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Ivan Partola
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TransactionDto {
    private int transactionId;
    private int sellerId;
    private int buyerId;
    @JsonProperty("dateTime")
    LocalDateTime createdTime;
    private int productId;
    private String txId;
}
