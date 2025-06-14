package org.example.usermanagement.service;

import org.example.usermanagement.entity.Order;
import org.example.usermanagement.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class OrderStatusScheduler {

    @Autowired
    private OrderService orderService;

    private final Random random = new Random();

    @Scheduled(fixedRate = 3000)
    public void checkOrderStatusTransitions() {
        LocalDateTime now = LocalDateTime.now();

        List<Order> orderedOrders = orderService.getOrdersByStatus(OrderStatus.ORDERED);
        for (Order order : orderedOrders) {
            if (isReadyForNextState(order, now, 10)) {
                int deviation = random.nextInt(5000);
                sleepWithDeviation(deviation);
                orderService.updateOrderStatus(order, OrderStatus.PREPARING);
            }
        }

        List<Order> preparingOrders = orderService.getOrdersByStatus(OrderStatus.PREPARING);
        for (Order order : preparingOrders) {
            if (isReadyForNextState(order, now, 20)) {
                int deviation = random.nextInt(5000);
                sleepWithDeviation(deviation);
                orderService.updateOrderStatus(order, OrderStatus.IN_DELIVERY);
            }
        }

        List<Order> inDeliveryOrders = orderService.getOrdersByStatus(OrderStatus.IN_DELIVERY);
        for (Order order : inDeliveryOrders) {
            if (isReadyForNextState(order, now, 20)) {
                int deviation = random.nextInt(5000);
                sleepWithDeviation(deviation);
                orderService.updateOrderStatus(order, OrderStatus.DELIVERED);
                order.setActive(false);
                orderService.updateOrder(order);
            }
        }
    }

    private boolean isReadyForNextState(Order order, LocalDateTime now, int seconds) {
        return order.getLastStatusChange().plusSeconds(seconds).isBefore(now);
    }

    private void sleepWithDeviation(int deviation) {
        try {
            Thread.sleep(deviation);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}