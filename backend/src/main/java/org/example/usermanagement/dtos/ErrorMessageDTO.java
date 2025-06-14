package org.example.usermanagement.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorMessageDTO {
    private Long id;
    private Long orderId;
    private String operation;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Long userId;
}