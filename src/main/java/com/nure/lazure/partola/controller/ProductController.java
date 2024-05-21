package com.nure.lazure.partola.controller;

import com.nure.lazure.partola.model.Product;
import com.nure.lazure.partola.service.ProductService;
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
public class ProductController {
    private final ProductService productService;
    @PostMapping
    public void add(@ModelAttribute Product product, HttpSession session) {
        productService.add(product, session);
    }

    @GetMapping("/wallet/{wallet}")
    public List<Product> getAllProductsByWallet(@PathVariable String wallet) {
        return productService.getAllProductsByWallet(wallet);
    }

    @GetMapping("/get-products")
    public List<Product> getProducts(@RequestParam(defaultValue = "40") int limit,
                                         @RequestParam Optional<String> title,
                                         @RequestParam Optional<List<Integer>> categoryId,
                                         @RequestParam Optional<Integer> offset) {
        return productService.getProducts(limit, title, categoryId, offset);
    }
}
