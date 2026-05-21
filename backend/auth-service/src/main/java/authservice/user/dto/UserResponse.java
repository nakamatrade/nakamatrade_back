package authservice.user.dto;

import authservice.user.domain.User;

public record UserResponse (
        String username,
        String birthDay,
        String gender,
        String email,
        String nickname
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getBirthDay(),
                user.getGender(),
                user.getEmail(),
                user.getNickname()
        );
    }
}