package authservice.user.dto;

import authservice.user.domain.User;

public record SignupResponse(
		Long id,
		String username
) {
	public static SignupResponse from(User user) {
		return new SignupResponse(
				user.getId(), 
				user.getUsername()
		);
	}
}
