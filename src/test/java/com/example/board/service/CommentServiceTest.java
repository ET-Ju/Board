package com.example.board.service;

import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.domain.User;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void create_존재하지_않는_post_id면_예외를_던진다() {
        String username = "작성자";
        User user = new User(username, "encodedPw", "작성자별명");
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.create(999L, "내용", username))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void create_정상적으로_댓글을_저장한다() {
        Long post_id = 1L;
        String comment_content = "내용";
        String post_author_username = "글쓴이";
        String comment_author_username = "댓글쓴이";

        User post_author = new User(post_author_username, "encodedPw", "글쓴이별명");
        User comment_author = new User(comment_author_username, "encodedPw2", "댓글쓴이별명");
        Post post = new Post("제목", "내용", post_author);
        Comment comment = new Comment(comment_content, comment_author, post);

        given(userRepository.findByUsername(comment_author_username)).willReturn(Optional.of(comment_author));
        given(postRepository.findById(post_id)).willReturn(Optional.of(post));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        commentService.create(post_id, comment_content, comment_author_username);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);

        verify(commentRepository).save(captor.capture());
        Comment saved = captor.getValue();

        assertThat(saved.getContent()).isEqualTo(comment_content);
        assertThat(saved.getUser()).isEqualTo(comment_author);
        assertThat(saved.getPost()).isEqualTo(post);
    }
}
