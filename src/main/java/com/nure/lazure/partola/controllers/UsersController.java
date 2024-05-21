package com.nure.lazure.partola.controllers;

import com.nure.lazure.partola.models.User;
import com.nure.lazure.partola.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {
    private final UsersService usersService;
    @GetMapping("/get-product-owner-wallet-by-product-id/{productId}")
    public User getProductOwnerWalletByProductId(@PathVariable int productId) {
        return usersService.getProductOwnerWalletByProductId(productId);
    }
}
