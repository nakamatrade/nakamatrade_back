package authservice.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record UsernameCheckRequest(
        @NotBlank(message = "아이디를 입력해주세요.") String username
) {}
