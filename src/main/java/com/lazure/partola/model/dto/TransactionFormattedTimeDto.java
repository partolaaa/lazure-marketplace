package com.lazure.partola.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Ivan Partola
 */
//TODO: implemented due to bug with accountsApi dateTime, should be fixed and refactored in future
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionFormattedTimeDto {
    private Long transactionId;
    private Long sellerId;
    private Long buyerId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    @JsonProperty("dateTime")
    LocalDateTime createdTime;
    private Long productId;
    private String txId;
}
