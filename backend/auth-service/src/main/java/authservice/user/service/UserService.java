package authservice.user.service;

import authservice.auth.dto.LoginRequest;
import authservice.global.exception.BusinessException;
import authservice.global.exception.ErrorCode;
import authservice.role.domain.Role;
import authservice.role.service.RoleService;
import authservice.user.domain.User;
import authservice.user.dto.SignupRequest;
import authservice.user.dto.SignupResponse;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final RoleService roleService;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        if (usernameExistsCheck(request.username())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        Role initialRole = roleService.getInitialRoleForSignup();
        
        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .birthDay(request.birthDay())
                .gender(request.gender())
                .role(initialRole)
                .build();

        User user = userRepository.save(newUser);
        return SignupResponse.from(user);
    }

    public int handleLoginFailure(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.recordLoginFailure();

        return user.getFailCount();
    }

    public void handleLoginSuccess(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.resetFailCount();
    }

    public boolean usernameExistsCheck(String username) {
        return userRepository.existsByUsername(username);
    }
}
