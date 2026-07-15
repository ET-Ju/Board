package com.example.board.controller;

import com.example.board.domain.Post;
import com.example.board.dto.PostCreateRequest;
import com.example.board.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/posts")
    public String list(@RequestParam(required = false) String keyword,
                       @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                       Model model){
        model.addAttribute("posts", postService.findAll(keyword, pageable));
        model.addAttribute("keyword", keyword);
        return "posts/list";
    }

    @GetMapping("/posts/new")
    public String newForm(Model model){
        model.addAttribute("postCreateRequest",
                new PostCreateRequest(null, null));
        return "posts/form";
    }

    @PostMapping("/posts")
    public String create(@Valid @ModelAttribute PostCreateRequest request,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetails userDetails){
        if (bindingResult.hasErrors()){
            return "posts/form";
        }

        postService.create(request.title(), request.content(), userDetails.getUsername());
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.findById(id));
        return "posts/detail";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/edit")
    public String editForm(@PathVariable Long id, Model model){
        model.addAttribute("post", postService.findById(id));
        return "posts/edit";
    }

    @PostMapping("/posts/{id}/edit")
    public String editPost(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String content,
                           @AuthenticationPrincipal UserDetails userDetails){
        postService.update(id, title, content, userDetails.getUsername());
        return "redirect:/posts/{id}";
    }
}
