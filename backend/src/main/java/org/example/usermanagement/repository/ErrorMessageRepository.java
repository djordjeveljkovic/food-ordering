package org.example.usermanagement.repository;

import org.example.usermanagement.entity.ErrorMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ErrorMessageRepository extends JpaRepository<ErrorMessage, Long> {
    List<ErrorMessage> findByOrderId(Long orderId);

    Page<ErrorMessage> findByOrderIdIn(List<Long> orderIds, Pageable pageable);
    Page<ErrorMessage> findByUserId(Long userId, Pageable pageable);
    @Query("SELECT o.id FROM Order o WHERE o.createdBy.id = :userId")
    List<Long> findOrderIdsByUserId(Long userId);
}
