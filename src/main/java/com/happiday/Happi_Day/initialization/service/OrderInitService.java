package com.happiday.Happi_Day.initialization.service;

import com.happiday.Happi_Day.domain.entity.product.*;
import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderInitService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SalesRepository salesRepository;
    private final DeliveryRepository deliveryRepository;
    private final ProductRepository productRepository;
    private final OrderedProductRepository orderedProductRepository;

    public void initOrders() {
        User buyer = userRepository.findById(3L).orElse(null);

        // 주문 1
        Sales sales1 = salesRepository.findById(1L).orElse(null);
        Product product1 = productRepository.findById(1L).orElse(null);
        Delivery delivery1 = deliveryRepository.findById(1L).orElse(null);

        Order order1 = orderRepository.save(buildOrder(buyer, sales1, delivery1));
        OrderedProduct orderedProduct1 = orderedProductRepository.save(buildOrderedProduct(product1, order1));
        order1.updateOrderedProduct(orderedProduct1);

        // 주문 2
        Sales sales2 = salesRepository.findById(2L).orElse(null);
        Product product2 = productRepository.findById(2L).orElse(null);
        Delivery delivery2 = deliveryRepository.findById(2L).orElse(null);

        Order order2 = orderRepository.save(buildOrder(buyer, sales2, delivery2));
        OrderedProduct orderedProduct2 = orderedProductRepository.save(buildOrderedProduct(product2, order2));
        order2.updateOrderedProduct(orderedProduct2);
    }

    private static OrderedProduct buildOrderedProduct(Product product, Order order) {
        return OrderedProduct.builder()
                .order(order)
                .product(product)
                .quantity(2)
                .build();
    }

    private static Order buildOrder(User buyer, Sales sales, Delivery delivery) {
        return Order.builder()
                .user(buyer)
                .sales(sales)
                .address("서울특별시 송파구 올림픽로 991")
                .totalPrice(24000)
                .orderStatus(OrderStatus.DELIVERING)
                .orderedAt(LocalDateTime.now())
                .trackingNum("해피택배 123412341234")
                .delivery(delivery)
                .refundAccountName("OO은행")
                .refundAccountNumber("123456789")
                .refundAccountUser("qwer")
                .orderedProducts(new ArrayList<>())
                .build();
    }
}
