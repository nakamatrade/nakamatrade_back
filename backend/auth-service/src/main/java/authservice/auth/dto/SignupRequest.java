package authservice.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequest(
        @NotBlank(message = "아이디는 필수 입력 입니다.") String username,
        @NotBlank(message = "비밀번호는 필수 입력 입니다.") 
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~@#$%^&+=!])(?=\\S+$).{8,15}$",
        		message = "비밀번호는 영문자와 숫자, 특수문자를 1개 이상 포함한 8-15자를 입력하여야 합니다.") String password,
        @NotBlank(message = "생년월일은 필수 입력 입니다.") String birthDay,
        String gender
) {}