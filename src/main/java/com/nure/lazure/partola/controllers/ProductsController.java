package com.nure.lazure.partola.controllers;

import com.nure.lazure.partola.models.Product;
import com.nure.lazure.partola.services.ProductsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author Ivan Partola
 */
@RequiredArgsConstructor
@RequestMapping("/api/products")
@RestController
public class ProductsController {
    private final ProductsService productsService;
    @PostMapping("")
    public void add(@ModelAttribute Product product, HttpSession session) {
        productsService.add(product, session);
    }

    @GetMapping("/wallet/{wallet}")
    public List<Product> getAllProductsByWallet(@PathVariable String wallet) {
        return productsService.getAllProductsByWallet(wallet);
    }

    @GetMapping("/get-products")
    public List<Product> getProducts(@RequestParam(defaultValue = "40") int limit,
                                         @RequestParam Optional<String> title,
                                         @RequestParam Optional<List<Integer>> categoryId,
                                         @RequestParam Optional<Integer> offset) {
        return productsService.getProducts(limit, title, categoryId, offset);
    }
}
