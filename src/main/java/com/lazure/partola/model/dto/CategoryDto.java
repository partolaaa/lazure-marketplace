package com.lazure.partola.model.dto;

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
    private int categoryId;
    private String name;
    private String description;
}
