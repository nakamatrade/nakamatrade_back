package authservice.auth.service;

import authservice.user.domain.Role;
import authservice.user.repository.RoleRepository;

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
import authservice.global.security.CustomUserDetails;
import authservice.global.security.JwtProvider;
import authservice.user.domain.User;
import authservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new CustomAuthException(ErrorCode.DUPLICATE_USERNAME);
        }
        
        Role role = roleRepository.findByType("USER").orElseThrow(()-> new CustomAuthException(ErrorCode.ROLE_NOT_FOUND));

        User newUser = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .brithDay(request.birthDay())
                .gender(request.gender())
                .build();

        newUser.setRole(role);

        userRepository.save(newUser);
    }
    
    @Transactional(noRollbackFor = CustomAuthException.class)
    public TokenDto login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();
            
            user.resetFailCount();

            return jwtProvider.generateToken(authentication);
        } catch (BadCredentialsException e) {
        	User user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new CustomAuthException(ErrorCode.USER_NOT_FOUND));

            user.recordLoginFailure();
            
            String errorMsg = "비밀번호가 일치하지 않습니다. (실패 횟수: " + user.getFailCount() + ")";
            throw new CustomAuthException(ErrorCode.INVALID_PASSWORD, errorMsg);
        } catch (LockedException e) {
        	throw new CustomAuthException(ErrorCode.ACCOUNT_LOCKED);
        }
    }
    
    public boolean usernameExistsCheck(SignupRequest request) {
        return userRepository.existsByUsername(request.username());
    }
}