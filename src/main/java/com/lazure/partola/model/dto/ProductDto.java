package com.lazure.partola.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Ivan Partola
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDto {
    @JsonProperty("product_id")
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    @JsonProperty("resource_link")
    private String resourceLink;
    @JsonProperty("category_id")
    private Integer categoryId;
    private String walletId;
}
