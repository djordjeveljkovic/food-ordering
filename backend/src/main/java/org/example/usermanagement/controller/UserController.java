package org.example.usermanagement.controller;

import org.example.usermanagement.dtos.UserDTO;
import org.example.usermanagement.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('CAN_READ_USERS')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('CAN_CREATE_USERS')")
    @PostMapping
    public ResponseEntity<String> createUser(@RequestBody UserDTO userDTO) {
        userService.createUser(userDTO);
        return ResponseEntity.ok("User created successfully");
    }

    @PreAuthorize("hasAuthority('CAN_UPDATE_USERS')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO, @AuthenticationPrincipal UserDetails currentUserDetails) {
        String newToken = userService.updateUser(id, userDTO, currentUserDetails);

        if (newToken == null) {
            return ResponseEntity.ok("User updated successfully");
        }

        return ResponseEntity.ok(newToken);
    }

    @PreAuthorize("hasAuthority('CAN_DELETE_USERS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
