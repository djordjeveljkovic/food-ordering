package org.example.usermanagement.repository;

import org.example.usermanagement.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"createdBy"})
    List<Order> findByCreatedById(Long userId);

    @EntityGraph(attributePaths = {"createdBy"})
    Page<Order> findAllByCreatedById(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"createdBy", "items"})
    Page<Order> findAll(Pageable pageable);
}

