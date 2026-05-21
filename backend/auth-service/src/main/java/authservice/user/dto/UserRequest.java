package authservice.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest (
        @Schema(description = "비밀번호 (영문, 숫자, 특수문자 포함 8~15자)", example = "!a123456")
        @NotBlank(message = "비밀번호는 필수 입력 입니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~@#$%^&+=!])(?=\\S+$).{8,15}$",
                message = "비밀번호는 영문자와 숫자, 특수문자를 1개 이상 포함한 8-15자를 입력하여야 합니다.")
        String password,

        @Schema(description = "생년월일", example = "1960-01-01")
        @NotBlank(message = "생년월일은 필수 입력 입니다.")
        String birthDay,

        @Schema(description = "성별", example = "Male")
        @NotBlank(message = "성별은 필수 입력 입니다.")
        String gender,

        @Schema(description = "이메일 주소", example = "nakama0@google.com")
        @NotBlank(message = "이메일 주소는 필수 입력 입니다.")
        String email,

        @Schema(description = "사용자 별명", example = "nakama")
        @NotBlank(message = "닉네임은 필수 입력입니다.")
        String nickname
) {
}
