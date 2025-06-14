package org.example.usermanagement.service;

import org.example.usermanagement.dtos.DishDTO;
import org.example.usermanagement.dtos.OrderDTO;
import org.example.usermanagement.dtos.UserDTO;
import org.example.usermanagement.entity.*;
import org.example.usermanagement.repository.DishRepository;
import org.example.usermanagement.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private final DishRepository dishRepository;

    @Autowired
    private ErrorService errorService;

    private static final int MAX_ACTIVE_ORDERS = 3;

    public OrderService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    public Page<OrderDTO> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(this::mapToOrderDTO);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    public void updateOrderStatus(Order order, OrderStatus newStatus) {
        order.setStatus(newStatus);
        order.setLastStatusChange(LocalDateTime.now());
        orderRepository.save(order);
    }

    public Page<OrderDTO> getOrdersByUserId(Long userId, Pageable pageable) {
        return orderRepository.findAllByCreatedById(userId, pageable).map(this::mapToOrderDTO);
    }

    public Order placeOrder(List<Long> itemIds, User user) {
        if (getActiveOrderCount() >= MAX_ACTIVE_ORDERS) {
            errorService.logError(null, user, "ORDER_CREATION", "Maximum number of active orders reached.");
            return null;
        }

        List<Dish> dishes = dishRepository.findAllById(itemIds);

        if (dishes.isEmpty()) {
            throw new IllegalArgumentException("No dishes found for the provided IDs");
        }

        Order order = new Order();
        order.setItems(dishes);
        order.setStatus(OrderStatus.ORDERED);
        order.setCreatedBy(user);
        order.setActive(true);
        order.setCreatedDate(LocalDateTime.now());

        return orderRepository.save(order);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getStatus() == OrderStatus.ORDERED) {
                order.setStatus(OrderStatus.CANCELED);
                order.setActive(false);
                orderRepository.save(order);
            } else {
                throw new IllegalStateException("Only orders in ORDERED status can be canceled.");
            }
        } else {
            throw new IllegalArgumentException("Order not found with id: " + orderId);
        }
    }

    public List<OrderDTO> searchOrders(OrderStatus status, LocalDateTime dateFrom, LocalDateTime dateTo, Long userId, boolean isAdmin, Long currentUserId) {
        return orderRepository.findAll().stream()
                .filter(order -> (status == null || order.getStatus() == status))
                .filter(order -> (dateFrom == null || order.getCreatedDate().isAfter(dateFrom)))
                .filter(order -> (dateTo == null || order.getCreatedDate().isBefore(dateTo)))
                .filter(order -> (isAdmin ? (userId == null || order.getCreatedBy().getId().equals(userId))
                        : order.getCreatedBy().getId().equals(currentUserId)))
                .map(this::mapToOrderDTO)
                .toList();
    }

    public Order scheduleOrder(List<Long> itemIds, User user, LocalDateTime scheduledFor) {

        if (scheduledFor != null && scheduledFor.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Scheduled time must be in the future.");
        }

        List<Dish> dishes = dishRepository.findAllById(itemIds);

        if (dishes.isEmpty()) {
            throw new IllegalArgumentException("No dishes found for the provided IDs");
        }

        Order order = new Order();
        order.setItems(dishes);
        order.setStatus(OrderStatus.SCHEDULED);
        order.setCreatedBy(user);
        order.setScheduledFor(scheduledFor);
        order.setActive(false);

        return orderRepository.save(order);
    }


    private boolean validateOrderLimit() {
        if (getActiveOrderCount() >= MAX_ACTIVE_ORDERS) {
            return false;
        }
        return true;
    }

    private int getActiveOrderCount() {
        return (int) orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.PREPARING || order.getStatus() == OrderStatus.IN_DELIVERY)
                .count();
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void processScheduledOrders() {
        List<Order> scheduledOrders = orderRepository.findAll().stream()
                .filter(order -> order.getStatus() == OrderStatus.SCHEDULED)
                .filter(order -> order.getScheduledFor() != null && order.getScheduledFor().isBefore(LocalDateTime.now()))
                .toList();

        for (Order order : scheduledOrders) {
            try {
                if (validateOrderLimit()) {
                    order.setStatus(OrderStatus.PREPARING);
                    order.setActive(true);
                    orderRepository.save(order);
                } else {
                    errorService.logError(order.getId(), order.getCreatedBy(), "SCHEDULE", "Maximum number of active orders reached.");
                    order.setStatus(OrderStatus.CANCELED);
                    order.setActive(false);
                    orderRepository.save(order);
                }
            } catch (IllegalStateException e) {
                errorService.logError(order.getId(), order.getCreatedBy(), "SCHEDULE", e.getMessage());
            }
        }
    }


    private OrderDTO mapToOrderDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setStatus(order.getStatus().name());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(order.getCreatedBy().getId());
        userDTO.setFirstName(order.getCreatedBy().getFirstName());
        userDTO.setLastName(order.getCreatedBy().getLastName());
        userDTO.setEmail(order.getCreatedBy().getEmail());
        dto.setCreatedBy(userDTO);

        List<DishDTO> dishDTOs = order.getItems().stream().map(dish -> {
            DishDTO dishDTO = new DishDTO();
            dishDTO.setId(dish.getId());
            dishDTO.setName(dish.getName());
            return dishDTO;
        }).collect(Collectors.toList());
        dto.setItems(dishDTOs);

        dto.setScheduledFor(order.getScheduledFor() != null ? order.getScheduledFor().toString() : "Not Scheduled");
        dto.setCreatedDate(order.getCreatedDate().toString());

        return dto;
    }

}