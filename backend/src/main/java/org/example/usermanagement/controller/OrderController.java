package org.example.usermanagement.controller;

import org.example.usermanagement.dtos.OrderCreationRequest;
import org.example.usermanagement.dtos.OrderDTO;
import org.example.usermanagement.entity.Order;
import org.example.usermanagement.entity.OrderStatus;
import org.example.usermanagement.entity.User;
import org.example.usermanagement.service.OrderService;
import org.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('CAN_SEARCH_ORDER')")
    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal
    ) {
        Pageable pageable = PageRequest.of(page, size);

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<OrderDTO> orders = currentUser.isAdmin()
                ? orderService.getAllOrders(pageable)
                : orderService.getOrdersByUserId(currentUser.getId(), pageable);

        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAuthority('CAN_SEARCH_ORDER')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('CAN_PLACE_ORDER')")
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderCreationRequest request, Principal principal) {
        List<Long> itemIds = request.getItemIds();

        if (itemIds == null || itemIds.isEmpty()) {
            throw new IllegalArgumentException("Item IDs cannot be null or empty");
        }

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order createdOrder = orderService.placeOrder(itemIds, user);

        if (createdOrder == null) {
            return ResponseEntity.status(400).body("Maximum number of active orders reached.");
        }

        return ResponseEntity.ok(Map.of(
                "id", createdOrder.getId(),
                "status", createdOrder.getStatus().toString(),
                "createdBy", createdOrder.getCreatedBy().getEmail()
        ));
    }

    @PreAuthorize("hasAuthority('CAN_TRACK_ORDER')")
    @GetMapping("/track/{id}")
    public ResponseEntity<Map<String, String>> trackOrder(@PathVariable Long id, Principal principal) {
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Order> order = orderService.getOrderById(id);

        System.out.println("Returning order: " + order.toString());

        if (order.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of("status", order.get().getStatus().name()));
    }

    @PreAuthorize("hasAuthority('CAN_SCHEDULE_ORDER')")
    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleOrder(@RequestBody Map<String, Object> payload, Principal principal) {
        List<Long> itemIds = (List<Long>)payload.get("itemIds");
        String scheduleDate = (String)payload.get("scheduleDate");

        if (itemIds == null || itemIds.isEmpty()) {
            throw new IllegalArgumentException("Item IDs cannot be null or empty");
        }

        User user = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDateTime scheduledFor = scheduleDate != null ? LocalDateTime.parse(scheduleDate) : null;
        Order scheduledOrder = orderService.scheduleOrder(itemIds, user, scheduledFor);

        if (scheduledOrder == null) {
            return ResponseEntity.status(400).body("Maximum number of active orders reached.");
        }

        return ResponseEntity.ok(Map.of(
                "id", scheduledOrder.getId(),
                "status", scheduledOrder.getStatus().toString(),
                "scheduledFor", scheduledFor
        ));
    }

    @PreAuthorize("hasAuthority('CAN_CANCEL_ORDER')")
    @PutMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('CAN_SEARCH_ORDER')")
    @GetMapping("/search")
    public ResponseEntity<List<OrderDTO>> searchOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(required = false) Long userId,
            Principal principal) {

        OrderStatus parsedStatus = null;
        LocalDateTime parsedDateFrom = null;
        LocalDateTime parsedDateTo = null;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

        if (status != null && !"null".equals(status)) {
            parsedStatus = OrderStatus.valueOf(status);
        }

        if (dateFrom != null && !"null".equals(dateFrom) && !dateFrom.isEmpty()) {
            parsedDateFrom = ZonedDateTime.parse(dateFrom, formatter).toLocalDateTime();
        }
        if (dateTo != null && !"null".equals(dateTo) && !dateTo.isEmpty()) {
            parsedDateTo = ZonedDateTime.parse(dateTo, formatter).toLocalDateTime();
        }

        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = currentUser.isAdmin();
        Long currentUserId = currentUser.getId();

        List<OrderDTO> orders = orderService.searchOrders(parsedStatus, parsedDateFrom, parsedDateTo, userId, isAdmin, currentUserId);
        return ResponseEntity.ok(orders);
    }

}