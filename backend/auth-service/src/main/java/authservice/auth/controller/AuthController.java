package authservice.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authservice.auth.dto.LoginRequest;
import authservice.auth.dto.SignupRequest;
import authservice.auth.dto.TokenDto;
import authservice.auth.dto.UsernameCheckRequest;
import authservice.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final String SIGNUP_SUCCESS_MESSAGE = "회원가입이 완료되었습니다.";

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(SIGNUP_SUCCESS_MESSAGE);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/exists")
    public ResponseEntity<Boolean> usernameExists(@Valid @RequestBody UsernameCheckRequest request) {
        return ResponseEntity.ok(authService.isUsernameTaken(request.username()));
    }
}
