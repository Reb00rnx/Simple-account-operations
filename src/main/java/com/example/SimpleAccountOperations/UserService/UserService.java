package com.example.SimpleAccountOperations.UserService;
import org.springframework.security.core.Authentication;

import com.example.SimpleAccountOperations.UserInfo.UserInfo;
import com.example.SimpleAccountOperations.UserRepository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserInfo getUserInfoByEmail(String email) {
        return userRepository.
                findByEmail(email).orElseThrow(()-> new IllegalArgumentException("Email not found!"));
    }

    public List<UserInfo> findUsersByLastName(String lastName) {
        return userRepository.findByLastName(lastName);
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if ("anonymousUser".equals(principal)) return null;
        return auth.getName(); 
    }

    public boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
