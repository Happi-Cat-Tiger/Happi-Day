package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.product.Delivery;
import com.happiday.Happi_Day.domain.entity.product.Sales;
import com.happiday.Happi_Day.domain.entity.product.dto.CreateDeliveryDto;
import com.happiday.Happi_Day.domain.entity.product.dto.ReadDeliveryDto;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.DeliveryRepository;
import com.happiday.Happi_Day.domain.repository.SalesRepository;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DeliveryService {
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;
    private final SalesRepository salesRepository;

    @Transactional
    public List<ReadDeliveryDto> createDelivery(Long salesId, CreateDeliveryDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
        // user 확인
        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        Delivery newDelivery = Delivery.createDelivery(sales, dto);
        deliveryRepository.save(newDelivery);
        List<Delivery> deliveryList = deliveryRepository.findAllBySales(sales);
        return deliveryList.stream().map(ReadDeliveryDto::fromEntity).collect(Collectors.toList());
    }

    public List<ReadDeliveryDto> readDelivery(Long salesId, String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
        // user 확인
        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        List<Delivery> deliveryList = deliveryRepository.findAllBySales(sales);
        return deliveryList.stream().map(ReadDeliveryDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public List<ReadDeliveryDto> deleteDelivery(Long salesId, Long deliveryId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Sales sales = salesRepository.findById(salesId)
                .orElseThrow(() -> new CustomException(ErrorCode.SALES_NOT_FOUND));
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(ErrorCode.DELIVERY_NOT_FOUND));
        // user 확인
        if(!user.equals(sales.getUsers())) throw new CustomException(ErrorCode.FORBIDDEN);

        deliveryRepository.delete(delivery);

        List<Delivery> deliveryList = deliveryRepository.findAllBySales(sales);
        return deliveryList.stream().map(ReadDeliveryDto::fromEntity).collect(Collectors.toList());
    }
}
