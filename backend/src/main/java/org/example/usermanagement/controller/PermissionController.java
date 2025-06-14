package org.example.usermanagement.controller;

import org.example.usermanagement.entity.Permission;
import org.example.usermanagement.repository.PermissionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllPermissions() {
        List<String> permissions = permissionRepository.findAll()
                .stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(permissions);
    }
}
