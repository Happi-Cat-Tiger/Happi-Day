//package com.happiday.Happi_Day.domain.controller;
//
//import com.happiday.Happi_Day.domain.entity.product.dto.CreateProductDto;
//import com.happiday.Happi_Day.domain.entity.product.dto.ReadProductDto;
//import com.happiday.Happi_Day.domain.service.ProductService;
//import com.happiday.Happi_Day.utils.SecurityUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/v1/{salesId}/products")
//@RequiredArgsConstructor
//public class ProductController {
//    private final ProductService productService;
//
//    // product 생성
//    @PostMapping()
//    public ResponseEntity<ReadProductDto> createProduct(
//            @PathVariable("salesId") Long salesId,
//            @RequestPart(name="product")CreateProductDto productDto){
//        String username = SecurityUtils.getCurrentUsername();
//        ReadProductDto response = productService.createProduct(salesId, productDto, username);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    // product 수정
//    @PutMapping("/{productId}")
//    public ResponseEntity<ReadProductDto> updateProduct(
//            @PathVariable("salesId") Long salesId,
//            @PathVariable("productId") Long productId,
//            @RequestPart(name="updateProduct") CreateProductDto productDto){
//        String username = SecurityUtils.getCurrentUsername();
//        ReadProductDto response = productService.updateProduct(salesId, productId, productDto, username);
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    // product 삭제
//    @DeleteMapping("{productId}")
//    public ResponseEntity<String> deleteProduct(
//            @PathVariable("salesId") Long salesId,
//            @PathVariable("productId") Long productId){
//        String username = SecurityUtils.getCurrentUsername();
//        productService.deleteProduct(salesId, productId, username);
//        return new ResponseEntity<>("삭제 완료되었습니다.",HttpStatus.OK);
//    }
//}
