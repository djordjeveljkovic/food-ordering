package org.example.usermanagement.controller;

import org.example.usermanagement.dtos.LoginRequest;
import org.example.usermanagement.dtos.LoginResponse;
import org.example.usermanagement.dtos.RegisterRequest;
import org.example.usermanagement.dtos.RegisterResponse;
import org.example.usermanagement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.authenticate(request.getEmail(), request.getPassword());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        RegisterResponse response = new RegisterResponse();
        response.setMessage("User registered successfully.");
        return ResponseEntity.ok(response);
    }
}