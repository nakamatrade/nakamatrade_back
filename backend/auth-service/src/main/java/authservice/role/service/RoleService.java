package authservice.role.service;

import org.springframework.stereotype.Service;

import authservice.global.exception.BusinessException;
import authservice.global.exception.ErrorCode;
import authservice.role.domain.Role;
import authservice.role.domain.RoleType;
import authservice.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {
	
	private final RoleRepository roleRepository;
	
	public Role getInitialRoleForSignup() {
		return roleRepository.findByType(RoleType.USER)
				.orElseThrow(()-> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
	}
}
