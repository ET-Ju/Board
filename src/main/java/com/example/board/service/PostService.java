package com.example.board.service;

import com.example.board.domain.Post;
import com.example.board.domain.User;
import com.example.board.dto.PostListResponse;
import com.example.board.dto.PostResponse;
import com.example.board.exception.PostNotFoundException;
import com.example.board.repository.PostRepository;
import com.example.board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    //private static final Logger log = LoggerFactory.getLogger(PostService.class);
    //위 내용을 @Slf4j가 대신 해줌.

    @Transactional
    public Long create(String title, String content, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        Post post = new Post(title, content, user);
        Long savedId = postRepository.save(post).getId();

        log.info("게시글 작성 완료. id={}, 작성자={}", savedId, username);

        return savedId;
    }

    public Page<PostListResponse> findAll(String keyword, Pageable pageable){
        return postRepository.searchWithCommentCount(keyword, pageable);
    }

    public PostResponse findById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new PostNotFoundException(id));
        return PostResponse.from(post);
    }

    @Transactional
    public void delete(Long id){
        postRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, String title, String content, String username){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        if (!post.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인 글만 수정할 수 있습니다.");
        }

        post.update(title, content);
    }
}
