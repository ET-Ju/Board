package com.example.board.repository;

import com.example.board.domain.Post;
import com.example.board.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {
    //Page<Post> findByTitleContaining(String keyword, Pageable pageable);

}
