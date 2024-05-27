package com.lazure.partola.mapper;

import com.lazure.partola.model.dto.FullTransactionInfoDto;
import com.lazure.partola.model.dto.ProductDto;
import com.lazure.partola.model.dto.TransactionDto;
import com.lazure.partola.model.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Ivan Partola
 */
@Mapper(componentModel = "spring")
public abstract class TransactionMapper {
    @Mapping(target = "buyerWalletId", source = "buyer.walletId")
    @Mapping(target = "sellerWalletId", source = "seller.walletId")
    @Mapping(target = "productDto", source = "productDto")
    public abstract FullTransactionInfoDto toFullTransactionInfoDto(TransactionDto transactionDto, UserDto buyer, UserDto seller, ProductDto productDto);
}
