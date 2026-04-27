package authservice.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import authservice.auth.dto.LoginRequest;
import authservice.auth.dto.SignupRequest;
import authservice.auth.dto.TokenDto;
import authservice.global.exception.CustomAuthException;
import authservice.global.exception.ErrorCode;
import authservice.global.security.NakamaUserDetails;
import authservice.global.security.JwtProvider;
import authservice.user.domain.Role;
import authservice.user.domain.User;
import authservice.user.repository.RoleRepository;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String DEFAULT_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest request) {
        validateDuplicateUsername(request.username());
        Role role = findDefaultRole();
        User newUser = buildUser(request, role);
        userRepository.save(newUser);
    }

    @Transactional(noRollbackFor = CustomAuthException.class)
    public TokenDto login(LoginRequest request) {
        try {
            Authentication authentication = authenticate(request.username(), request.password());
            recordLoginSuccess(authentication);
            return jwtProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
            recordLoginFailure(request.username());
            throw new CustomAuthException(ErrorCode.INVALID_PASSWORD);
        } catch (LockedException e) {
            throw new CustomAuthException(ErrorCode.ACCOUNT_LOCKED);
        }
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    private void validateDuplicateUsername(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new CustomAuthException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    private Role findDefaultRole() {
        return roleRepository.findByType(DEFAULT_ROLE)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.ROLE_NOT_FOUND));
    }

    private User buildUser(SignupRequest request, Role role) {
        return User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .birthDate(request.birthDate())
                .gender(request.gender())
                .role(role)
                .build();
    }

    private Authentication authenticate(String username, String password) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    private void recordLoginSuccess(Authentication authentication) {
        NakamaUserDetails userDetails = (NakamaUserDetails) authentication.getPrincipal();
        userDetails.recordLoginSuccess();
    }

    private void recordLoginFailure(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));
        user.recordLoginFailure();
    }
}
