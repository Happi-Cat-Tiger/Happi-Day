//package com.happiday.Happi_Day.domain.controller;
//
//import com.happiday.Happi_Day.domain.entity.product.dto.CreateDeliveryDto;
//import com.happiday.Happi_Day.domain.entity.product.dto.ReadDeliveryDto;
//import com.happiday.Happi_Day.domain.service.DeliveryService;
//import com.happiday.Happi_Day.utils.SecurityUtils;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/{salesId}/delivery")
//@RequiredArgsConstructor
//public class DeliveryController {
//    private final DeliveryService deliveryService;
//
//    // 배달방법 추가
//    @PostMapping
//    public ResponseEntity<List<ReadDeliveryDto>> createDelivery(
//            @PathVariable("salesId") Long salesId,
//            @RequestPart(name="delivery")CreateDeliveryDto delivery){
//        String username = SecurityUtils.getCurrentUsername();
//        List<ReadDeliveryDto> response = deliveryService.createDelivery(salesId, delivery, username);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    // 배달방법 조회
//    @GetMapping
//    public ResponseEntity<List<ReadDeliveryDto>> readDelivery(
//            @PathVariable("salesId") Long salesId){
//        String username = SecurityUtils.getCurrentUsername();
//        List<ReadDeliveryDto> response = deliveryService.readDelivery(salesId, username);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//
//    // 배달방법 삭제
//    @DeleteMapping("/{deliveryId}")
//    public ResponseEntity<List<ReadDeliveryDto>> deleteDelivery(
//            @PathVariable("salesId")Long salesId,
//            @PathVariable("deliveryId")Long deliveryId){
//        String username = SecurityUtils.getCurrentUsername();
//        List<ReadDeliveryDto> response = deliveryService.deleteDelivery(salesId, deliveryId, username);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//}
