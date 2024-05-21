package com.lazure.partola.model;

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
public class Product {
    @JsonProperty("product_id")
    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;

    @JsonProperty("resource_link")
    private String resourceLink;

    @JsonProperty("category_id")
    private Integer categoryId;
}
