package com.example.board.dto;

import com.example.board.domain.Comment;
import com.example.board.domain.User;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String authorUsername,
        String authorNickname,
        LocalDateTime createdAt
) {
    public static CommentResponse from(Comment comment) {
        User user = comment.getUser();

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                user.getUsername(),
                user.getNickname(),
                comment.getCreatedAt()
        );
    }
}
