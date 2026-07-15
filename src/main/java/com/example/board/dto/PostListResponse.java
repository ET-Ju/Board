package com.example.board.dto;

import com.example.board.domain.Post;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String title,
        String author,
        LocalDateTime createdAt,
        Long commentCount
) {
}
