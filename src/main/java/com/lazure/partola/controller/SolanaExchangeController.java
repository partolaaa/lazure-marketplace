package com.lazure.partola.controller;

import com.lazure.partola.model.dto.SolanaPriceDto;
import com.lazure.partola.service.SolanaExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/${solana.api.url.path}")
@RequiredArgsConstructor
public class SolanaExchangeController {
    private final SolanaExchangeService solanaExchangeService;

    @GetMapping("/price")
    public SolanaPriceDto getSolanaPrice() {
        return solanaExchangeService.getSolanaPrice();
    }
}
