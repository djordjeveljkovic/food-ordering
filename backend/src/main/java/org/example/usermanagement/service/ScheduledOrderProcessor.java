package org.example.usermanagement.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledOrderProcessor {

    private final OrderService orderService;

    public ScheduledOrderProcessor(OrderService orderService) {
        this.orderService = orderService;
    }

    @Scheduled(fixedRate = 3000)
    public void processOrders() {
        orderService.processScheduledOrders();
    }
}