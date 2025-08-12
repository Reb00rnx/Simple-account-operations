package com.example.SimpleAccountOperations.UserSecurity;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        // user/admin
                        .requestMatchers(HttpMethod.POST, "/change_password","/user/change-status").hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/users/search", "/user/status").hasAnyRole("USER","ADMIN")
                        //admin
                        .requestMatchers(HttpMethod.DELETE, "/delete_account").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/users").hasAnyRole("ADMIN")


                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}


