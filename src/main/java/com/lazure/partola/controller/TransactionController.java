package com.lazure.partola.controller;

import com.lazure.partola.model.dto.FullTransactionInfoDto;
import com.lazure.partola.model.dto.TransactionDto;
import com.lazure.partola.service.TransactionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Ivan Partola
 */
@RestController
@RequestMapping("/api/${accounts.api.url.path.transactions}")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    @GetMapping
    public List<FullTransactionInfoDto> getTransactionsByWalletId(@RequestParam String walletId) {
        return transactionService.getTransactionsByWalletId(walletId);
    }

    @PostMapping
    public void add(@RequestBody TransactionDto transactionDto, HttpSession session) {
        transactionService.add(transactionDto, session);
    }
}
