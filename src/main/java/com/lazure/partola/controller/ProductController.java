package com.lazure.partola.controller;

import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.criteria.ProductCriteria;
import com.lazure.partola.service.ProductQueryService;
import com.lazure.partola.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Ivan Partola
 */
@RequiredArgsConstructor
@RequestMapping("/api/${products.api.url.path.products}")
@RestController
public class ProductController {
    private final ProductService productService;
    private final ProductQueryService productQueryService;
    @PostMapping
    public void add(@ModelAttribute ProductDto productDto, HttpSession session) {
        productService.add(productDto, session);
    }

    @GetMapping("/wallet/{wallet}")
    public List<ProductDto> getAllProductsByWallet(@PathVariable String wallet) {
        return productService.getAllProductsByWallet(wallet);
    }

    @GetMapping
    public List<ProductDto> getProducts(@RequestParam(defaultValue = "40") int limit,
                                        @RequestParam(defaultValue = "0") int offset,
                                        ProductCriteria productCriteria) {
        return productQueryService.getProducts(limit, offset, productCriteria);
    }

    @GetMapping("/{productId}")
    public ProductDto getProduct(@PathVariable Long productId, HttpSession session) {
        return productService.getProductById(productId, session);
    }
}
