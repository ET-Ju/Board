package com.example.board.repository;

import com.example.board.config.QuerydslConfig;
import com.example.board.domain.Comment;
import com.example.board.domain.Post;
import com.example.board.domain.User;
import com.example.board.dto.PostListResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
public class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TestEntityManager em;

    @Test
    void searchWithCommentCount_각_글의_댓글_수를_센다() {
        User user = em.persist(new User("사용자명", "encodedPw", "사용자별명"));
        Post springPost = em.persist(new Post("스프링 입문", "내용", user));
        em.persist(new Comment("댓글1", user, springPost));
        em.persist(new Comment("댓글2", user, springPost));

        em.persist(new Post("스프링 부트", "내용", user));
        em.persist(new Post("자바 기초", "내용", user));

        Page<PostListResponse> result = postRepository.searchWithCommentCount("스프링", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(PostListResponse::title)
                .allMatch(title -> title.contains("스프링"));

        assertThat(result.getContent())
                .extracting(PostListResponse::commentCount)
                .containsExactlyInAnyOrder(2L, 0L);
    }


    @Test
    void searchWithCommentCount_단일_단어로_제목을_검색한다() {
        User user = em.persist(new User("사용자명", "encodedPw", "사용자별명"));
        Post springPost = em.persist(new Post("스프링 입문", "내용", user));
        em.persist(new Post("스프링 부트", "내용", user));
        em.persist(new Post("자바 기초", "내용", user));
        em.persist(new Post("스프링 스프링", "내용", user));

        Page<PostListResponse> result = postRepository.searchWithCommentCount("스프링", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getContent())
                .extracting(PostListResponse::title)
                .allMatch(title -> title.contains("스프링"));
    }


    @Test
    void searchWithCommentCount_여러_단어가_모두_포함된_글만_검색한다() {
        User user = em.persist(new User("사용자명", "encodedPw", "사용자별명"));
        Post springPost = em.persist(new Post("스프링 입문", "내용", user));
        em.persist(new Post("스프링 부트", "내용", user));
        em.persist(new Post("자바 기초", "내용", user));
        em.persist(new Post("스프링 스프링", "내용", user));
        em.persist(new Post("부트 스프링", "내용", user));
        em.persist(new Post("부트스프링", "내용", user));
        em.persist(new Post("위로부트아래로스프링", "내용", user));

        Page<PostListResponse> result = postRepository.searchWithCommentCount("스프링 부트", PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(4);
        assertThat(result.getContent())
                .extracting(PostListResponse::title)
                .allMatch(title -> title.contains("스프링"))
                .allMatch(title -> title.contains("부트"));
    }

    @Test
    void searchWithCommentCount_검색어가_없으면_전체_글을_반환한다() {
        User user = em.persist(new User("사용자명", "encodedPw", "사용자별명"));
        Post springPost = em.persist(new Post("스프링 입문", "내용", user));
        em.persist(new Post("스프링 부트", "내용", user));
        em.persist(new Post("자바 기초", "내용", user));
        em.persist(new Post("스프링 스프링", "내용", user));
        em.persist(new Post("부트 스프링", "내용", user));
        em.persist(new Post("부트스프링", "내용", user));
        em.persist(new Post("위로부트아래로스프링", "내용", user));

        Page<PostListResponse> result = postRepository.searchWithCommentCount(null, PageRequest.of(0, 10));

        assertThat(result.getTotalElements()).isEqualTo(7);
    }
}
