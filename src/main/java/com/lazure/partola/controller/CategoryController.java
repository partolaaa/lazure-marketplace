package com.lazure.partola.controller;

import com.lazure.partola.model.dto.CategoryDto;
import com.lazure.partola.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/${products.api.url.path.categories}")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    @GetMapping
    public List<CategoryDto> getCategories() {
        return categoryService.getCategories();
    }
}
