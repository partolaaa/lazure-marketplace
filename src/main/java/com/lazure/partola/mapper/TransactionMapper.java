package com.lazure.partola.mapper;

import com.lazure.partola.model.dto.FullTransactionInfoDto;
import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.dto.TransactionDto;
import com.lazure.partola.model.dto.UserDto;
import com.lazure.partola.service.ProductService;
import com.lazure.partola.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Ivan Partola
 */
@Component
@RequiredArgsConstructor
public class TransactionMapper {
    private final UserService userService;
    private final ProductService productService;

    public FullTransactionInfoDto toFullTransactionInfoDto(TransactionDto transactionDto) {
        UserDto buyer = userService.getUserById(transactionDto.getBuyerId());
        UserDto seller = userService.getUserById(transactionDto.getSellerId());

        ProductDto productDto = productService.getProductByIdWithoutAuth(transactionDto.getProductId());

        return new FullTransactionInfoDto(
                buyer.getWalletId(),
                seller.getWalletId(),
                transactionDto.getCreatedTime(),
                transactionDto.getTxId(),
                productDto);
    }
}
