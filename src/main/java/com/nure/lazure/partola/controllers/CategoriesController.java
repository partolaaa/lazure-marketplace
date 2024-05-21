package com.nure.lazure.partola.controllers;

import com.nure.lazure.partola.models.Category;
import com.nure.lazure.partola.services.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoriesController {
    private final CategoriesService categoriesService;
    @GetMapping("")
    public List<Category> getCategories() {
        return categoriesService.getCategories();
    }
}
