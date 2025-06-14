package org.example.usermanagement.service;
import org.example.usermanagement.config.JwtTokenUtil;
import org.example.usermanagement.dtos.RegisterRequest;
import org.example.usermanagement.entity.Permission;
import org.example.usermanagement.entity.User;
import org.example.usermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public String authenticate(String email, String password) {
        System.out.println("Login 1 started:" + email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No email found"));

String hash = passwordEncoder.encode("admin123");  // or your password
System.out.println("sifra");
System.out.println(hash);
        System.out.println("Login 2 started:" + email);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Password doesn't match");
        }
        System.out.println("Login 3 started:" + email);

        List<String> permissions = user.getPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        return jwtTokenUtil.generateToken(user.getEmail(),user.isAdmin(), permissions);
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already in use.");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));


        userRepository.save(user);
    }
}
