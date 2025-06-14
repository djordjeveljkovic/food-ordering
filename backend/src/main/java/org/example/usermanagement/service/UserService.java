package org.example.usermanagement.service;

import org.example.usermanagement.config.JwtTokenUtil;
import org.example.usermanagement.dtos.UserDTO;
import org.example.usermanagement.entity.Permission;
import org.example.usermanagement.entity.User;
import org.example.usermanagement.repository.PermissionRepository;
import org.example.usermanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public UserService(UserRepository userRepository, PermissionRepository permissionRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAllWithPermissions();

        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    public void createUser(UserDTO userDTO) {
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setAdmin(userDTO.isAdmin());

        user.setPassword(passwordEncoder.encode(
                userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()
                        ? userDTO.getPassword()
                        : "defaultPassword"
        ));

        Set<Permission> permissions = userDTO.getPermissions().stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String updateUser(Long id, UserDTO userDTO, UserDetails currentUserDetails) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setAdmin(userDTO.isAdmin());

        Set<Permission> permissions = userDTO.getPermissions().stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());
        user.setPermissions(permissions);

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        userRepository.save(user);

        if (currentUserDetails.getUsername().equals(user.getEmail())) {
            List<String> permissionNames = permissions.stream().map(Permission::getName).toList();
            return jwtTokenUtil.generateToken(user.getEmail(), user.isAdmin(), permissionNames);
        }

        return null;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setAdmin(user.isAdmin());

        Set<String> permissionNames = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
        dto.setPermissions(permissionNames);

        return dto;
    }
}