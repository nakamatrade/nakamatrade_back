package authservice.role.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
	USER("USER", "일반 사용자"),
	ADMIN("ADMIN", "시스템 관리자");
	
	private final String name;
	private final String description;
}
