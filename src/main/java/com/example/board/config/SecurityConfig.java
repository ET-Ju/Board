package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/signup", "/login", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/posts/new").authenticated()
                        .requestMatchers("/posts/*/edit", "/posts/*/delete").authenticated()
                        .requestMatchers("/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.GET, "/posts", "/posts/*").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/posts")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/posts")
                        .permitAll()
                );
        return http.build();
    }
}
