package com.example.board.controller;


import com.example.board.dto.PostCreateRequest;
import com.example.board.dto.SignupRequest;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup_form(Model model) {
        model.addAttribute("signupRequest",
                new SignupRequest(null, null, null));
        return "users/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute SignupRequest request,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/signup";
        }
        userService.signup(request);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "users/login";
    }
}
