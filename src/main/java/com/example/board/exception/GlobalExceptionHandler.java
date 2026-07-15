package com.example.board.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public String handlePostNotFound(PostNotFoundException e, Model model) {
        log.warn("존재하지 않는 Post에 접근 시도: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("권한 없는 접근 시도: {}", e.getMessage());
        model.addAttribute("message", e.getMessage());
        return "error/403";
    }
}
