package com.example.board.repository;

import com.example.board.domain.QComment;
import com.example.board.domain.QPost;
import com.example.board.domain.QUser;
import com.example.board.dto.PostListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostListResponse> searchWithCommentCount(String keyword, Pageable pageable) {
        QPost post = QPost.post;
        QComment comment = QComment.comment;
        QUser user = QUser.user;

        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            for (String word : keyword.split("\\s+")) {
                if (!word.isBlank()) {
                    builder.and(post.title.contains(word));
                }
            }
        }

        List<PostListResponse> content = queryFactory
                .select(Projections.constructor(PostListResponse.class,
                        post.id,
                        post.title,
                        user.nickname,
                        post.createdAt,
                        JPAExpressions.select(comment.count())
                                .from(comment)
                                .where(comment.post.eq(post))
                ))
                .from(post)
                .join(post.user, user)
                .where(builder)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
