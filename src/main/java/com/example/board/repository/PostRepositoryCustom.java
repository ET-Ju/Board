package com.example.board.repository;

import com.example.board.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<PostListResponse> searchWithCommentCount(String keyword, Pageable pageable);
}
