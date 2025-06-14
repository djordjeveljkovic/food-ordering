package org.example.usermanagement.controller;

import org.example.usermanagement.dtos.ErrorMessageDTO;
import org.example.usermanagement.entity.ErrorMessage;
import org.example.usermanagement.entity.User;
import org.example.usermanagement.service.ErrorService;
import org.example.usermanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/errors")
public class ErrorController {

    @Autowired
    private ErrorService errorService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ErrorMessage>> getAllErrors() {
        return ResponseEntity.ok(errorService.getAllErrors());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<ErrorMessage>> getErrorsByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(errorService.getErrorsByOrderId(orderId));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<ErrorMessageDTO>> getErrorsWithPagination(
            Pageable pageable,
            Principal principal) {
        User currentUser = userService.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Page<ErrorMessage> errors = errorService.getErrorsByUser(currentUser.getId(), currentUser.isAdmin(), pageable);

        Page<ErrorMessageDTO> dtoPage = errors.map(errorService::mapToDTO);

        return ResponseEntity.ok(dtoPage);
    }

}