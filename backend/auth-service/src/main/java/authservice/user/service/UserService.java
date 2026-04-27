package authservice.user.service;

import authservice.auth.dto.LoginRequest;
import authservice.auth.dto.SignupRequest;
import authservice.global.exception.BusinessException;
import authservice.global.exception.ErrorCode;
import authservice.user.domain.Role;
import authservice.user.domain.User;
import authservice.user.repository.RoleRepository;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequest request) {
        if (usernameExistsCheck(request)) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        Role role = roleRepository.findByType("USER").orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .brithDay(request.birthDay())
                .gender(request.gender())
                .build();

        newUser.setRole(role);

        userRepository.save(newUser);
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

    public boolean usernameExistsCheck(SignupRequest request) {
        return userRepository.existsByUsername(request.username());
    }
}
