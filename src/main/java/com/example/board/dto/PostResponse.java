package com.example.board.dto;

import com.example.board.domain.Post;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        String authorUsername,
        String authorNickname,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentResponse> comments
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getUser().getUsername(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                post.getComments().stream().map(CommentResponse::from).toList()
        );
    }
}
