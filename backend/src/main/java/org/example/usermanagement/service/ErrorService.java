package org.example.usermanagement.service;

import org.example.usermanagement.dtos.ErrorMessageDTO;
import org.example.usermanagement.entity.ErrorMessage;
import org.example.usermanagement.entity.User;
import org.example.usermanagement.repository.ErrorMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErrorService {

    @Autowired
    private ErrorMessageRepository errorMessageRepository;

    public List<ErrorMessage> getAllErrors() {
        return errorMessageRepository.findAll();
    }

    public List<ErrorMessage> getErrorsByOrderId(Long orderId) {
        return errorMessageRepository.findByOrderId(orderId);
    }

    public Page<ErrorMessage> getErrorsByUser(Long userId, boolean isAdmin, Pageable pageable) {
        if (isAdmin) {
            return errorMessageRepository.findAll(pageable);
        } else {
            return errorMessageRepository.findByUserId(userId, pageable);
        }
    }

    public void logError(Long orderId, User user, String operation, String errorMessage) {
        ErrorMessage error = new ErrorMessage();
        error.setOrderId(orderId);
        error.setUser(user);
        error.setOperation(operation);
        error.setErrorMessage(errorMessage);
        error.setTimestamp(LocalDateTime.now());
        errorMessageRepository.save(error);
    }

    public ErrorMessageDTO mapToDTO(ErrorMessage errorMessage) {
        ErrorMessageDTO dto = new ErrorMessageDTO();
        dto.setId(errorMessage.getId());
        dto.setOrderId(errorMessage.getOrderId());
        dto.setOperation(errorMessage.getOperation());
        dto.setErrorMessage(errorMessage.getErrorMessage());
        dto.setTimestamp(errorMessage.getTimestamp());
        dto.setUserId(errorMessage.getUser().getId());
        return dto;
    }
}