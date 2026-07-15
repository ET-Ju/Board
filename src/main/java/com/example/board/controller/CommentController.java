package com.example.board.controller;


import com.example.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public String create(@PathVariable Long postId,
                         @RequestParam String content,
                         @AuthenticationPrincipal UserDetails userDetails) {
        commentService.create(postId, content, userDetails.getUsername());
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam Long postId,
                         @AuthenticationPrincipal UserDetails userDetails) {
        commentService.delete(id, userDetails.getUsername());
        return "redirect:/posts/" + postId;
    }
}
