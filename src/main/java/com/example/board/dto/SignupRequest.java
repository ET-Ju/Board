package com.example.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @NotBlank(message = "아이디를 입력해주세요.")
        @Size(min = 4, max = 20, message = "아이디는 4-20자여야 합니다.")
        String username,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        String password,

        @NotBlank(message = "닉네임을 입력해주세요.")
        String nickname
) {
}
