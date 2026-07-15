package com.example.board.service;

import com.example.board.domain.User;
import com.example.board.dto.SignupRequest;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signup(SignupRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), encodedPassword, request.nickname());
        return userRepository.save(user).getId();
    }
}
