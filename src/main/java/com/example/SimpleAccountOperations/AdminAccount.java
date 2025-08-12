package com.example.SimpleAccountOperations;

import com.example.SimpleAccountOperations.UserInfo.Role;
import com.example.SimpleAccountOperations.UserInfo.UserInfo;
import com.example.SimpleAccountOperations.UserRepository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AdminAccount implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminAccount(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@system.com")) {
            UserInfo admin = new UserInfo();
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setEmail("admin@system.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("000-000-000");
            admin.setRole(Role.ADMIN);
            admin.setStatus(true);
            userRepository.save(admin);
            System.out.println("Admin account created: admin@system.com / admin123");
        }
    }
}