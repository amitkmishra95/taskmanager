package com.app.controller;

import com.app.dto.UserDTO;
import com.app.entity.*;
import com.app.repository.UserRepository;
import com.app.security.JwtUtil;
import com.app.service.UserService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userrepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@Valid @RequestBody UserDTO dto) {

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        userService.register(user);

        return "User Registered";
    }
    @GetMapping("/test")
    public String test() {
        return "Working";
    }

    @PostMapping("/login")
    public User login(@RequestBody UserDTO dto) {

        User user = userService.getByEmail(dto.getEmail());

        if (!user.getPassword().equals(dto.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return user; 
    }
    
   
    @GetMapping("/users")
    public List<User> users() {
        return userrepo.findAll()
                .stream()
                .filter(u -> u.getRole().name().equals("MEMBER"))
                .toList();
    }
}