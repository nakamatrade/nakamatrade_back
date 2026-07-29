package authservice.user.service;

import authservice.auth.dto.LoginRequest;
import authservice.global.exception.BusinessException;
import authservice.global.exception.ErrorCode;
import authservice.role.domain.Role;
import authservice.role.service.RoleService;
import authservice.user.domain.User;
import authservice.user.dto.SignupRequest;
import authservice.user.dto.SignupResponse;
import authservice.user.dto.UserRequest;
import authservice.user.dto.UserResponse;
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
    
    @Transactional(readOnly = true)
    public User findByIdWithRoles(Long userId) {
    	return userRepository.findByIdWithRoles(userId)
    			.orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }
    
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validateForSignup(request);

        Role initialRole = roleService.getInitialRoleForSignup();
        
        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .nickName(request.nickname())
                .birthDay(request.birthDay())
                .gender(request.gender())
                .role(initialRole)
                .build();

        User user = userRepository.save(newUser);
        return SignupResponse.from(user);
    }

    @Transactional
    public void updateUser(Long userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new BusinessException(ErrorCode.USER_NOT_FOUND));

        validateForUpdate(user, request);

        user.update(
                passwordEncoder.encode(request.password())
                , request.birthDay()
                , request.gender()
                , request.email()
                , request.nickname()
        );
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

    private void validateForSignup(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    private void validateForUpdate(User user, UserRequest request) {
        if (!user.getEmail().equals(request.email())
                && userRepository.existsByEmail(request.email())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        if (!user.getNickname().equals(request.nickname())
                && userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }
}
