package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.domain.User;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long create(Long postId, String content, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));
        Comment comment = new Comment(content, user, post);
        return commentRepository.save(comment).getId();
    }

    @Transactional
    public void delete(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 없습니다. id=" + commentId));

        if (!comment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
