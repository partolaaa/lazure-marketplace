package com.lazure.partola.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ivan Partola
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryDto {
    @JsonProperty("category_id")
    private int categoryId;
    private String name;
    private String description;
}
