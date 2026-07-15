package com.example.board.service;

import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.CommentRepository;
import com.example.board.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void findById_존재하지_않는_id면_예외를_던진다() {
        given(postRepository.findById(anyLong())).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.findById(999L))
                .isInstanceOf(PostNotFoundException.class);
    }


}
