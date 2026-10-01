package com.militaryasset.controller;

import com.militaryasset.entity.User;
import com.militaryasset.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(
            @RequestParam String username,
            @RequestParam String password) {

        User user = userRepository.findByUsername(username).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (user == null || !"ACTIVE".equals(user.getStatus())
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            response.put("authenticated", false);
            return ResponseEntity.status(401).body(response);
        }

        response.put("authenticated", true);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("fullName", user.getFullName());
        response.put("role", user.getRole().getName());
        response.put("baseId", user.getBase() == null ? null : user.getBase().getId());

        return ResponseEntity.ok(response);
    }
}
