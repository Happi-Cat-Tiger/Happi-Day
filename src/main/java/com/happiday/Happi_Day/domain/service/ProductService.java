package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.product.Product;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateProductDto;
import com.happiday.Happi_Day.domain.entity.product.dto.ReadProductDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.ProductRepository;
import com.happiday.Happi_Day.domain.repository.SalesRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {
    private final UserRepository userRepository;
    private final SalesRepository salesRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ReadProductDto createProduct(Long salesId, CreateProductDto productDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
        // user 확인
        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        Product newProduct = Product.createProduct(sales, productDto);
        productRepository.save(newProduct);
        return ReadProductDto.fromEntity(newProduct);
    }

    @Transactional
    public ReadProductDto updateProduct(Long salesId, Long productId, CreateProductDto productDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if(!sales.getProducts().contains(product)) throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        // user 확인
        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        product.update(productDto.toEntity());
        productRepository.save(product);

        return ReadProductDto.fromEntity(product);
    }

//    @Transactional
//    public void deleteProduct(Long salesId, Long productId, String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//
//        Sales sales = salesRepository.findById(salesId)
//                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//
//        if(!sales.getProducts().contains(product)) throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
//        // user 확인
//        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);
//
//        productRepository.deleteById(productId);
//    }
}
